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

    // Mock local draft generation for Phase 1 Walking Skeleton
    suspend fun generateLocalMockDraft(reportId: String, typedNotes: String?) {
        val existing = reportDao.getReportById(reportId) ?: return

        val workCompleted = listOf(
            "Replaced damaged cabinet hinge and realigned door assembly.",
            "Tested door movement and verified smooth latching operation."
        )
        val findings = listOf(
            "Minor moisture marks visible near cabinet base from prior minor sink overflow."
        )
        val recommendations = listOf(
            "Monitor adjacent under-sink piping and schedule an inspection if moisture returns."
        )
        val summary = "Completed kitchen cabinet hinge replacement and aligned cabinet door. Checked surrounding area for moisture."

        val updated = existing.copy(
            status = ReportStatus.NEEDS_REVIEW,
            customerSummary = summary,
            workCompletedJson = workCompleted.joinToString("||"),
            findingsJson = findings.joinToString("||"),
            recommendationsJson = recommendations.joinToString("||"),
            rawTranscript = typedNotes ?: "Replaced damaged cabinet hinge and realigned door assembly.",
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
