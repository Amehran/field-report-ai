package com.fieldreport.ai.ui.viewmodel

import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.AiAgentMode
import com.fieldreport.ai.data.model.PhotoLabel
import com.fieldreport.ai.data.model.ReportStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class ReportViewModel {
    private val _aiAgentMode = MutableStateFlow(AiAgentMode.CLOUD)
    actual val aiAgentMode: Flow<AiAgentMode> = _aiAgentMode.asStateFlow()

    private val _businessName = MutableStateFlow("Apex Field Services")
    actual val businessName: Flow<String> = _businessName.asStateFlow()

    private val _currency = MutableStateFlow("$")
    actual val currency: Flow<String> = _currency.asStateFlow()

    private val _technicianName = MutableStateFlow("Alex Morgan")
    actual val technicianName: Flow<String> = _technicianName.asStateFlow()

    private val _allReports = MutableStateFlow<List<ReportEntity>>(emptyList())
    actual val allReports: StateFlow<List<ReportEntity>> = _allReports.asStateFlow()

    private val _currentReportId = MutableStateFlow<String?>(null)
    actual val currentReportId: StateFlow<String?> = _currentReportId.asStateFlow()

    private val _currentReport = MutableStateFlow<ReportEntity?>(null)
    actual val currentReport: StateFlow<ReportEntity?> = _currentReport.asStateFlow()

    private val _currentMedia = MutableStateFlow<List<MediaItemEntity>>(emptyList())
    actual val currentMedia: StateFlow<List<MediaItemEntity>> = _currentMedia.asStateFlow()

    actual fun setAiAgentMode(mode: AiAgentMode) {
        _aiAgentMode.value = mode
    }

    actual fun setBusinessName(name: String) {
        _businessName.value = name
    }

    actual fun setCurrency(symbol: String) {
        _currency.value = symbol
    }

    actual fun setTechnicianName(name: String) {
        _technicianName.value = name
    }

    actual fun startNewReport(customerName: String, jobTitle: String, address: String?) {
        val newId = "rep_" + (1000..9999).random()
        val report = ReportEntity(
            id = newId,
            userId = "usr_ios",
            status = ReportStatus.DRAFT,
            customerName = customerName,
            jobTitle = jobTitle,
            address = address
        )
        _currentReportId.value = newId
        _currentReport.value = report
        _allReports.value = _allReports.value + report
    }

    actual fun setCurrentReportId(id: String) {
        _currentReportId.value = id
        _currentReport.value = _allReports.value.find { it.id == id }
    }

    actual fun addPhoto(uriString: String, label: PhotoLabel) {
        val reportId = _currentReportId.value ?: return
        val media = MediaItemEntity(
            id = "med_" + (1000..9999).random(),
            reportId = reportId,
            type = com.fieldreport.ai.data.model.MediaType.PHOTO,
            label = label,
            localUri = uriString
        )
        _currentMedia.value = _currentMedia.value + media
    }

    actual fun togglePhotoLabel(item: MediaItemEntity) {
        val nextLabel = when (item.label) {
            PhotoLabel.BEFORE -> PhotoLabel.AFTER
            PhotoLabel.AFTER -> PhotoLabel.GENERAL
            PhotoLabel.GENERAL -> PhotoLabel.BEFORE
        }
        _currentMedia.value = _currentMedia.value.map {
            if (it.id == item.id) it.copy(label = nextLabel) else it
        }
    }

    actual fun setAudioRecording(uriString: String) {
        val report = _currentReport.value ?: return
        _currentReport.value = report.copy(audioLocalUri = uriString)
    }

    actual fun updateReportHeader(customerName: String, jobTitle: String) {
        val report = _currentReport.value ?: return
        _currentReport.value = report.copy(customerName = customerName, jobTitle = jobTitle)
    }

    actual fun updateCosts(laborCost: Double?, partsCost: Double?, totalCost: Double?) {
        val report = _currentReport.value ?: return
        _currentReport.value = report.copy(laborCost = laborCost, partsCost = partsCost, totalCost = totalCost)
    }

    actual fun generateReportDraft(customerName: String, jobTitle: String, typedNotes: String?) {
        val report = _currentReport.value ?: return
        _currentReport.value = report.copy(
            customerName = customerName,
            jobTitle = jobTitle,
            typedNotes = typedNotes,
            status = ReportStatus.NEEDS_REVIEW
        )
    }

    actual fun updateWorkCompleted(items: List<String>) {
        val report = _currentReport.value ?: return
        _currentReport.value = report.copy(workCompletedJson = items.joinToString("||"))
    }

    actual fun updateFindings(items: List<String>) {
        val report = _currentReport.value ?: return
        _currentReport.value = report.copy(findingsJson = items.joinToString("||"))
    }

    actual fun updateRecommendations(items: List<String>) {
        val report = _currentReport.value ?: return
        _currentReport.value = report.copy(recommendationsJson = items.joinToString("||"))
    }

    actual fun updateReportReviewData(
        issueDescription: String?,
        workDoneText: String?,
        technicianComments: String?,
        technicianName: String?
    ) {
        val report = _currentReport.value ?: return
        _currentReport.value = report.copy(
            initialStatus = issueDescription,
            resolutionStepsJson = workDoneText,
            technicianComments = technicianComments,
            technicianName = technicianName
        )
    }

    actual fun approveReport() {
        val report = _currentReport.value ?: return
        _currentReport.value = report.copy(status = ReportStatus.APPROVED)
    }

    actual fun deleteReport(reportId: String) {
        _allReports.value = _allReports.value.filter { it.id != reportId }
        if (_currentReportId.value == reportId) {
            _currentReportId.value = null
            _currentReport.value = null
        }
    }

    actual fun deleteAllReports() {
        _allReports.value = emptyList()
        _currentReportId.value = null
        _currentReport.value = null
    }
}
