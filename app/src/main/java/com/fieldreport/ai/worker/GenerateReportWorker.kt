package com.fieldreport.ai.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fieldreport.ai.data.db.AppDatabase
import com.fieldreport.ai.data.model.ReportStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class GenerateReportWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val reportId = inputData.getString("reportId") ?: return Result.failure()
        
        val db = AppDatabase.getDatabase(applicationContext)
        val reportDao = db.reportDao()
        
        val report = reportDao.getReportById(reportId) ?: return Result.failure()
        val mediaItems = reportDao.getMediaItemsForReport(reportId)
        
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser ?: return Result.failure() // Must be authenticated
        
        val storage = FirebaseStorage.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        
        try {
            // Update status to uploading/waiting online if not already approved or higher
            if (report.status != ReportStatus.APPROVED && report.status != ReportStatus.GENERATED && report.status != ReportStatus.SHARED && report.status != ReportStatus.COMPLETED) {
                reportDao.updateStatus(reportId, ReportStatus.WAITING_ONLINE)
            }
            
            // Upload Media Items
            for (media in mediaItems) {
                if (media.isUploaded) continue
                
                val rawUri = Uri.parse(media.localUri)
                val fileUri = if (media.localUri.startsWith("content://") || media.localUri.startsWith("file://")) {
                    compressImageUri(applicationContext, rawUri)
                } else {
                    rawUri
                }
                
                val storageRef = storage.reference.child("users/${user.uid}/reports/$reportId/media/${media.id}")
                storageRef.putFile(fileUri).await()
                
                val storagePath = storageRef.path
                db.reportDao().updateMediaStoragePath(media.id, storagePath, true)
            }
            
            // Upload Audio if present
            var audioPath = report.audioStoragePath
            if (report.audioLocalUri != null && report.audioStoragePath == null) {
                val audioUri = Uri.parse(report.audioLocalUri)
                val audioRef = storage.reference.child("users/${user.uid}/reports/$reportId/audio/recording.m4a")
                audioRef.putFile(audioUri).await()
                audioPath = audioRef.path
                reportDao.updateReportAudioStoragePath(reportId, audioPath)
            }
            
            // Upload Report Metadata to Firestore
            val reportDoc = hashMapOf(
                "id" to report.id,
                "userId" to report.userId,
                "status" to report.status.name,
                "customerName" to report.customerName,
                "jobTitle" to report.jobTitle,
                "address" to report.address,
                "typedNotes" to report.typedNotes,
                "audioStoragePath" to audioPath,
                "createdAt" to report.createdAt,
                "updatedAt" to System.currentTimeMillis()
            )
            
            firestore.collection("users").document(user.uid)
                .collection("reports").document(reportId)
                .set(reportDoc).await()
                
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }

    private fun compressImageUri(context: Context, originalUri: Uri): Uri {
        return try {
            val inputStream = context.contentResolver.openInputStream(originalUri) ?: return originalUri
            val options = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            android.graphics.BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            val maxDimension = 1600
            var sampleSize = 1
            if (options.outHeight > maxDimension || options.outWidth > maxDimension) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while (halfHeight / sampleSize >= maxDimension || halfWidth / sampleSize >= maxDimension) {
                    sampleSize *= 2
                }
            }

            val decodeOptions = android.graphics.BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            val streamForDecode = context.contentResolver.openInputStream(originalUri) ?: return originalUri
            val bitmap = android.graphics.BitmapFactory.decodeStream(streamForDecode, null, decodeOptions)
            streamForDecode.close()

            if (bitmap == null) return originalUri

            val compressedDir = File(context.cacheDir, "compressed_media").apply { if (!exists()) mkdirs() }
            val compressedFile = File(compressedDir, "comp_${System.currentTimeMillis()}.jpg")
            val outStream = java.io.FileOutputStream(compressedFile)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outStream)
            outStream.flush()
            outStream.close()
            bitmap.recycle()

            Uri.fromFile(compressedFile)
        } catch (e: Exception) {
            e.printStackTrace()
            originalUri
        }
    }
}
