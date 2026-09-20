package com.fieldreport.ai.ui.viewmodel

import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.AiAgentMode
import com.fieldreport.ai.data.model.PhotoLabel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

expect class ReportViewModel {
    val aiAgentMode: Flow<AiAgentMode>
    val businessName: Flow<String>
    val currency: Flow<String>
    val technicianName: Flow<String>
    val allReports: StateFlow<List<ReportEntity>>
    val currentReportId: StateFlow<String?>
    val currentReport: StateFlow<ReportEntity?>
    val currentMedia: StateFlow<List<MediaItemEntity>>

    fun setAiAgentMode(mode: AiAgentMode)
    fun setBusinessName(name: String)
    fun setCurrency(symbol: String)
    fun setTechnicianName(name: String)
    fun startNewReport(customerName: String = "", jobTitle: String = "", address: String? = null)
    fun setCurrentReportId(id: String)
    fun addPhoto(uriString: String, label: PhotoLabel)
    fun togglePhotoLabel(item: MediaItemEntity)
    fun setAudioRecording(uriString: String)
    fun updateReportHeader(customerName: String, jobTitle: String)
    fun updateCosts(laborCost: Double?, partsCost: Double?, totalCost: Double?)
    fun generateReportDraft(customerName: String, jobTitle: String, typedNotes: String?)
    fun updateWorkCompleted(items: List<String>)
    fun updateFindings(items: List<String>)
    fun updateRecommendations(items: List<String>)
    fun updateReportReviewData(issueDescription: String?, workDoneText: String?, technicianComments: String?, technicianName: String?)
    fun approveReport()
    fun deleteReport(reportId: String)
    fun deleteAllReports()
}
