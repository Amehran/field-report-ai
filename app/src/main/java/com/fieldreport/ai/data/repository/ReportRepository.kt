package com.fieldreport.ai.data.repository

import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportDao
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.ReportStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ReportRepository(private val reportDao: ReportDao) {

    val allReports: Flow<List<ReportEntity>> = reportDao.getAllReports()

    fun getReportById(reportId: String): Flow<ReportEntity?> = reportDao.observeReportById(reportId)

    fun getMediaForReport(reportId: String): Flow<List<MediaItemEntity>> = reportDao.getMediaForReport(reportId)

    suspend fun saveReport(report: ReportEntity) {
        reportDao.insertReport(report)
    }

    suspend fun updateReport(report: ReportEntity) {
        reportDao.updateReport(report)
    }

    suspend fun addMediaItem(item: MediaItemEntity) {
        reportDao.insertMediaItem(item)
    }

    suspend fun deleteMediaItem(item: MediaItemEntity) {
        reportDao.deleteMediaItem(item)
    }

    suspend fun updateMediaStoragePath(mediaId: String, path: String, isUploaded: Boolean) {
        reportDao.updateMediaStoragePath(mediaId, path, isUploaded)
    }

    suspend fun updateReportAudioStoragePath(reportId: String, path: String) {
        reportDao.updateReportAudioStoragePath(reportId, path)
    }

    suspend fun setAudioRecording(reportId: String, uriString: String) {
        reportDao.updateAudioLocalUri(reportId, uriString)
    }

    // Dynamic local draft generation for offline/fallback mode
    suspend fun generateLocalMockDraft(reportId: String, typedNotes: String?) {
        val existing = reportDao.getReportById(reportId) ?: return

        val job = existing.jobTitle.ifBlank { "Service Work" }
        val notes = typedNotes?.takeIf { it.isNotBlank() }
        val hasAudio = existing.audioLocalUri != null

        val workCompleted = mutableListOf<String>()
        if (notes != null) {
            workCompleted.add(notes)
            workCompleted.add("Performed inspection and verified operational status for $job.")
        } else if (hasAudio) {
            workCompleted.add("Completed recorded voice note tasks for $job.")
            workCompleted.add("Inspected equipment and verified smooth operation.")
        } else {
            workCompleted.add("Completed primary service tasks for $job.")
            workCompleted.add("Verified quality of work and tested functionality.")
        }

        val findings = mutableListOf<String>()
        if (notes != null) {
            findings.add("Notes recorded: \"$notes\"")
        } else if (hasAudio) {
            findings.add("Voice note audio recording captured and stored.")
        } else {
            findings.add("All system components inspected during $job.")
        }

        val recommendations = listOf(
            "Perform regular maintenance check for $job within 6 months.",
            "Contact technician if any unusual issues or noise return."
        )

        val summary = "Completed $job for ${existing.customerName}."

        val updated = existing.copy(
            status = ReportStatus.NEEDS_REVIEW,
            customerSummary = summary,
            workCompletedJson = workCompleted.joinToString("||"),
            findingsJson = findings.joinToString("||"),
            recommendationsJson = recommendations.joinToString("||"),
            rawTranscript = notes ?: (if (hasAudio) "Voice recording captured." else "Technician notes for $job."),
            updatedAt = System.currentTimeMillis()
        )
        reportDao.updateReport(updated)
    }

    suspend fun approveReport(reportId: String) {
        val existing = reportDao.getReportById(reportId) ?: return
        val updated = existing.copy(
            status = ReportStatus.APPROVED,
            approvedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        reportDao.updateReport(updated)
    }
}
