package com.fieldreport.ai.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.fieldreport.ai.data.db.AppDatabase
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.MediaType
import com.fieldreport.ai.data.model.PhotoLabel
import com.fieldreport.ai.data.model.ReportStatus
import com.fieldreport.ai.data.repository.ReportRepository
import com.fieldreport.ai.pdf.PdfReportGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReportRepository = ReportRepository(
        AppDatabase.getDatabase(application).reportDao()
    )

    val allReports: StateFlow<List<ReportEntity>> = repository.allReports.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _currentReportId = MutableStateFlow<String?>(null)
    val currentReportId: StateFlow<String?> = _currentReportId.asStateFlow()

    val currentReport: StateFlow<ReportEntity?> = _currentReportId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.getReportById(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentMedia: StateFlow<List<MediaItemEntity>> = _currentReportId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getMediaForReport(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startNewReport(customerName: String, jobTitle: String, address: String? = null) {
        val newId = UUID.randomUUID().toString()
        val newReport = ReportEntity(
            id = newId,
            userId = "usr_demo",
            status = ReportStatus.DRAFT,
            customerName = customerName.ifBlank { "Miller Residence" },
            jobTitle = jobTitle.ifBlank { "Kitchen repair" },
            address = address
        )
        viewModelScope.launch {
            repository.saveReport(newReport)
            _currentReportId.value = newId
        }
    }

    fun setCurrentReportId(id: String) {
        _currentReportId.value = id
    }

    fun addPhoto(uriString: String, label: PhotoLabel = PhotoLabel.BEFORE) {
        val reportId = _currentReportId.value ?: return
        val mediaItem = MediaItemEntity(
            id = UUID.randomUUID().toString(),
            reportId = reportId,
            type = MediaType.PHOTO,
            label = label,
            localUri = uriString,
            sortOrder = 0
        )
        viewModelScope.launch {
            repository.addMediaItem(mediaItem)
        }
    }

    fun togglePhotoLabel(item: MediaItemEntity) {
        val nextLabel = when (item.label) {
            PhotoLabel.BEFORE -> PhotoLabel.AFTER
            PhotoLabel.AFTER -> PhotoLabel.GENERAL
            PhotoLabel.GENERAL -> PhotoLabel.BEFORE
        }
        viewModelScope.launch {
            repository.addMediaItem(item.copy(label = nextLabel))
        }
    }

    fun setAudioRecording(uriString: String) {
        val reportId = _currentReportId.value ?: return
        viewModelScope.launch {
            repository.setAudioRecording(reportId, uriString)
        }
    }

    fun generateReportDraft(typedNotes: String?) {
        val reportId = _currentReportId.value ?: return
        
        viewModelScope.launch {
            val report = currentReport.value ?: return@launch
            
            // Set state to generating
            repository.updateReport(report.copy(status = ReportStatus.GENERATING, updatedAt = System.currentTimeMillis()))
            
            try {
                val mediaItems = currentMedia.value
                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser
                
                if (user == null) {
                    repository.generateLocalMockDraft(reportId, typedNotes)
                    return@launch
                }

                val storage = FirebaseStorage.getInstance()
                val storageMediaUris = mutableListOf<String>()
                
                // Upload Photo Media safely (only valid file/content URIs)
                for (media in mediaItems) {
                    if (media.localUri.startsWith("content://") || media.localUri.startsWith("file://")) {
                        if (!media.isUploaded) {
                            try {
                                val fileUri = Uri.parse(media.localUri)
                                val storageRef = storage.reference.child("users/${user.uid}/reports/$reportId/media/${media.id}")
                                storageRef.putFile(fileUri).await()
                                repository.updateMediaStoragePath(media.id, storageRef.path, true)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        storageMediaUris.add("gs://${storage.reference.bucket}/users/${user.uid}/reports/$reportId/media/${media.id}")
                    }
                }
                
                // Upload Audio safely
                if (report.audioLocalUri != null && (report.audioLocalUri.startsWith("content://") || report.audioLocalUri.startsWith("file://"))) {
                    if (report.audioStoragePath == null) {
                        try {
                            val audioUri = Uri.parse(report.audioLocalUri)
                            val audioRef = storage.reference.child("users/${user.uid}/reports/$reportId/audio/recording.m4a")
                            audioRef.putFile(audioUri).await()
                            repository.updateReportAudioStoragePath(reportId, audioRef.path)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    storageMediaUris.add("gs://${storage.reference.bucket}/users/${user.uid}/reports/$reportId/audio/recording.m4a")
                }
                
                // Get Firebase ID Token & Call Fastify Backend
                val tokenResult = user.getIdToken(true).await()
                val idToken = tokenResult.token
                
                if (idToken != null) {
                    val client = OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build()
                        
                    val jsonBody = JSONObject().apply {
                        put("jobTitle", report.jobTitle)
                        put("customerName", report.customerName)
                        if (typedNotes != null) put("typedNotes", typedNotes)
                        put("mediaUris", JSONArray(storageMediaUris))
                    }
                    
                    val request = Request.Builder()
                        .url("http://10.0.2.2:8080/v1/reports/generate")
                        .addHeader("Authorization", "Bearer $idToken")
                        .addHeader("X-Idempotency-Key", reportId)
                        .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                        .build()
                        
                    val response = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { 
                        client.newCall(request).execute() 
                    }
                    
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (!responseBody.isNullOrBlank()) {
                            val draftJson = JSONObject(responseBody)
                            val newReport = report.copy(
                                workCompletedJson = draftJson.optString("workCompletedJson", ""),
                                findingsJson = draftJson.optString("findingsJson", ""),
                                recommendationsJson = draftJson.optString("recommendationsJson", ""),
                                status = ReportStatus.NEEDS_REVIEW,
                                updatedAt = System.currentTimeMillis()
                            )
                            repository.updateReport(newReport)
                            return@launch
                        }
                    }
                }
                
                // Fallback to local draft generator if API response or token fails
                repository.generateLocalMockDraft(reportId, typedNotes)
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback safely to ensure draft is always created without crashing
                repository.generateLocalMockDraft(reportId, typedNotes)
            }
        }
    }

    fun updateWorkCompleted(items: List<String>) {
        val report = currentReport.value ?: return
        viewModelScope.launch {
            repository.updateReport(
                report.copy(
                    workCompletedJson = items.joinToString("||"),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateFindings(items: List<String>) {
        val report = currentReport.value ?: return
        viewModelScope.launch {
            repository.updateReport(
                report.copy(
                    findingsJson = items.joinToString("||"),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateRecommendations(items: List<String>) {
        val report = currentReport.value ?: return
        viewModelScope.launch {
            repository.updateReport(
                report.copy(
                    recommendationsJson = items.joinToString("||"),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun approveReport() {
        val reportId = _currentReportId.value ?: return
        viewModelScope.launch {
            repository.approveReport(reportId)
            
            try {
                // Enqueue Cloud Sync
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                    
                val syncRequest = OneTimeWorkRequestBuilder<com.fieldreport.ai.worker.GenerateReportWorker>()
                    .setInputData(Data.Builder().putString("reportId", reportId).build())
                    .setConstraints(constraints)
                    .build()
                    
                WorkManager.getInstance(getApplication()).enqueue(syncRequest)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun shareReportPdf(context: Context) {
        val report = currentReport.value ?: return
        val media = currentMedia.value
        try {
            val pdfFile = PdfReportGenerator.generatePdf(context, report, media)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Job Completion Report — ${report.customerName}")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hi ${report.customerName}, here is your job report for ${report.jobTitle}."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Job Report PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(context, "Could not open PDF share sheet: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }
}
