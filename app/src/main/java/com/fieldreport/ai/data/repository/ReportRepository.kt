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

        val job = existing.jobTitle.ifBlank { "General Repair & Maintenance" }
        val notes = typedNotes?.trim()?.takeIf { it.isNotBlank() }
        val jobLower = job.lowercase()

        val workCompleted = mutableListOf<String>()
        val findings = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        if (jobLower.contains("thermostat") || jobLower.contains("hvac") || jobLower.contains("heating") || jobLower.contains("cooling") || jobLower.contains("climate")) {
            workCompleted.add("Inspected thermostat wiring, wall mounting plate, and HVAC control terminal connections.")
            workCompleted.add("Calibrated temperature sensors and tested heating and cooling cycle transitions.")
            if (notes != null) workCompleted.add("Service detail: $notes")

            findings.add("Thermostat control voltage and relay signals measured within nominal operating specs.")
            findings.add("Verified accurate ambient temperature sensing and responsive call-for-heat/cool relays.")

            recommendations.add("Replace HVAC air filter every 1 to 3 months to ensure optimal airflow and system efficiency.")
            recommendations.add("Schedule annual pre-season HVAC and thermostat maintenance inspection.")
        } else if (jobLower.contains("plumb") || jobLower.contains("leak") || jobLower.contains("pipe") || jobLower.contains("drain") || jobLower.contains("faucet")) {
            workCompleted.add("Inspected water supply lines, shut-off valves, fittings, and drainage connections.")
            workCompleted.add("Cleared restriction and tested system under static and dynamic operating water pressure.")
            if (notes != null) workCompleted.add("Service detail: $notes")

            findings.add("Water pressure measured within safe operating limits; no active leaks or moisture detected.")
            findings.add("Fittings, seals, and pipe joints confirmed structurally sound.")

            recommendations.add("Periodically inspect valve connections and supply lines for signs of wear or moisture.")
            recommendations.add("Avoid chemical drain cleaners to prevent pipe lining damage.")
        } else if (jobLower.contains("electr") || jobLower.contains("panel") || jobLower.contains("outlet") || jobLower.contains("breaker") || jobLower.contains("wire")) {
            workCompleted.add("Tested circuit continuity, line voltage, and ground integrity across terminals.")
            workCompleted.add("Secured electrical connections, checked breaker ratings, and verified safety shut-offs.")
            if (notes != null) workCompleted.add("Service detail: $notes")

            findings.add("Voltage and current draw measured within standard electrical safety tolerances.")
            findings.add("No thermal discoloration or insulation degradation detected on circuit conductors.")

            recommendations.add("Avoid overloading branch circuits with high-draw equipment.")
            recommendations.add("Perform periodic test of GFCI outlets and main breaker panel.")
        } else {
            workCompleted.add("Executed primary inspection, repair, and maintenance procedures for $job.")
            workCompleted.add("Tested system performance and verified operational readiness.")
            if (notes != null) workCompleted.add("Service detail: $notes")

            findings.add("Evaluated $job components; verified physical condition, wear levels, and connection integrity.")
            findings.add("All system parameters operating within normal operational tolerances.")

            recommendations.add("Schedule periodic maintenance check for $job within 6 to 12 months.")
            recommendations.add("Keep work area clean and monitor system for any unexpected noise or malfunction.")
        }

        val summary = "Completed $job for ${existing.customerName}."

        val updated = existing.copy(
            status = ReportStatus.NEEDS_REVIEW,
            customerSummary = summary,
            workCompletedJson = workCompleted.joinToString("||"),
            findingsJson = findings.joinToString("||"),
            recommendationsJson = recommendations.joinToString("||"),
            rawTranscript = notes ?: "",
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

    suspend fun deleteReport(reportId: String) {
        reportDao.deleteReport(reportId)
    }

    suspend fun deleteReportWithFiles(context: android.content.Context, reportId: String) {
        val report = reportDao.getReportById(reportId)
        val mediaItems = reportDao.getMediaItemsForReport(reportId)

        report?.audioLocalUri?.let { deleteFileFromUri(context, it) }
        mediaItems.forEach { deleteFileFromUri(context, it.localUri) }

        val pdfFile = java.io.File(context.cacheDir, "reports/Report_${reportId}.pdf")
        if (pdfFile.exists()) {
            pdfFile.delete()
        }

        reportDao.deleteReport(reportId)
    }

    suspend fun deleteAllReports() {
        reportDao.deleteAllReports()
    }

    suspend fun deleteAllReportsWithFiles(context: android.content.Context) {
        val reports = reportDao.getAllReportsList()
        reports.forEach { report ->
            deleteReportWithFiles(context, report.id)
        }
        reportDao.deleteAllReports()
    }

    private fun deleteFileFromUri(context: android.content.Context, uriStr: String) {
        try {
            val uri = android.net.Uri.parse(uriStr) ?: return
            if (uri.scheme == "content") {
                try {
                    context.contentResolver.delete(uri, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val path = uri.path
            if (path != null) {
                val file = java.io.File(path)
                if (file.exists()) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
