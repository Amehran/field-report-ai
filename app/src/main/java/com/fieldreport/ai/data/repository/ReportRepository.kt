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
    suspend fun generateLocalMockDraft(
        reportId: String,
        typedNotes: String?,
        tone: com.fieldreport.ai.data.model.ReportTone = com.fieldreport.ai.data.model.ReportTone.STANDARD
    ) {
        val existing = reportDao.getReportById(reportId) ?: return

        val job = existing.jobTitle.ifBlank { "General Repair & Maintenance" }
        val notes = typedNotes?.trim()?.takeIf { it.isNotBlank() }
        val jobLower = job.lowercase()

        val workCompleted = mutableListOf<String>()
        val findings = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        when (tone) {
            com.fieldreport.ai.data.model.ReportTone.INSURANCE -> {
                findings.add("[Insurance Claim Inspection] Cause of Damage: Sudden mechanical component failure under normal operating conditions.")
                findings.add("Pre-existing Condition Check: No evidence of long-term deferred maintenance prior to incident.")
                findings.add("Code Compliance: System verified against applicable municipal and safety standards.")
                workCompleted.add("Executed immediate containment and scope-of-loss remediation for $job.")
                if (notes != null) workCompleted.add("Claim details: $notes")
                workCompleted.add("Itemized replacement of damaged components to restore pre-loss condition.")
                recommendations.add("Submit itemized repair report and photo documentation directly to insurance claims adjuster.")
                recommendations.add("Retain replaced parts for adjuster physical inspection if requested.")
            }
            com.fieldreport.ai.data.model.ReportTone.TECHNICAL -> {
                findings.add("[Technical Specs] Evaluated $job operating parameters against factory specification tolerances.")
                findings.add("Measured input voltage: 120.2V AC | Operating current: nominal | Thermal delta: within 5% tolerance.")
                findings.add("Inspected relays, control signals, and mechanical wear surfaces.")
                workCompleted.add("Performed step-by-step diagnostic sequence for $job.")
                if (notes != null) workCompleted.add("Tech notes: $notes")
                workCompleted.add("Torqued mechanical fasteners and re-calibrated sensor setpoints.")
                recommendations.add("Perform secondary spectrum analysis and check resistance across primary terminals in 6 months.")
            }
            com.fieldreport.ai.data.model.ReportTone.CLIENT -> {
                findings.add("[Summary for Homeowner] We inspected your $job and found everything is in good working order!")
                findings.add("All key safety checks passed with flying colors.")
                workCompleted.add("Took care of your $job and completed all routine maintenance tasks.")
                if (notes != null) workCompleted.add("What we worked on: $notes")
                workCompleted.add("Double-checked our work to make sure your system runs smoothly and quietly.")
                recommendations.add("Keep the area around the unit clear of clutter for optimal performance.")
                recommendations.add("Give us a call anytime if you notice any unusual sounds or changes!")
            }
            com.fieldreport.ai.data.model.ReportTone.STANDARD -> {
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
            }
        }

        val summary = when (tone) {
            com.fieldreport.ai.data.model.ReportTone.INSURANCE -> "Insurance Claim Inspection Report for $job at ${existing.customerName}."
            com.fieldreport.ai.data.model.ReportTone.TECHNICAL -> "Detailed Technical Service Report for $job at ${existing.customerName}."
            com.fieldreport.ai.data.model.ReportTone.CLIENT -> "Customer Service Summary for $job prepared for ${existing.customerName}."
            com.fieldreport.ai.data.model.ReportTone.STANDARD -> "Completed $job for ${existing.customerName}."
        }

        val updated = existing.copy(
            status = ReportStatus.REPORT_CREATED,
            reportTone = tone.name,
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

    suspend fun markReportShared(reportId: String) {
        val existing = reportDao.getReportById(reportId) ?: return
        val updated = existing.copy(
            status = ReportStatus.SHARED,
            updatedAt = System.currentTimeMillis()
        )
        reportDao.updateReport(updated)
    }

    suspend fun deleteMediaItem(context: android.content.Context, item: MediaItemEntity) {
        deleteFileFromUri(context, item.localUri)
        item.storagePath?.let { deleteFileFromUri(context, it) }
        reportDao.deleteMediaItem(item)
    }

    suspend fun deleteReport(reportId: String) {
        reportDao.deleteReport(reportId)
    }

    suspend fun deleteReportWithFiles(context: android.content.Context, reportId: String) {
        val report = reportDao.getReportById(reportId)
        val mediaItems = reportDao.getMediaItemsForReport(reportId)

        // 1. Delete Audio Files
        report?.audioLocalUri?.let { deleteFileFromUri(context, it) }
        report?.audioStoragePath?.let { deleteFileFromUri(context, it) }

        // 2. Delete Media Items (Photos/Videos)
        mediaItems.forEach { item ->
            deleteFileFromUri(context, item.localUri)
            item.storagePath?.let { deleteFileFromUri(context, it) }
        }

        // 3. Delete Generated PDF Files across all storage locations
        val pdfInCache = java.io.File(context.cacheDir, "reports/Report_${reportId}.pdf")
        if (pdfInCache.exists()) pdfInCache.delete()

        val pdfInFiles = java.io.File(context.filesDir, "reports/Report_${reportId}.pdf")
        if (pdfInFiles.exists()) pdfInFiles.delete()

        context.externalCacheDir?.let { extDir ->
            val extPdf = java.io.File(extDir, "reports/Report_${reportId}.pdf")
            if (extPdf.exists()) extPdf.delete()
        }

        report?.pdfLocalPath?.let { path ->
            deleteFileFromUri(context, path)
        }

        // 4. Delete Database Record
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

        // Extra safety: clean up remaining files in all app storage directories
        try {
            java.io.File(context.cacheDir, "reports").deleteRecursively()
            java.io.File(context.cacheDir, "images").deleteRecursively()
            java.io.File(context.cacheDir, "recordings").deleteRecursively()
            java.io.File(context.cacheDir, "compressed_media").deleteRecursively()

            java.io.File(context.filesDir, "reports").deleteRecursively()
            java.io.File(context.filesDir, "images").deleteRecursively()
            java.io.File(context.filesDir, "recordings").deleteRecursively()

            context.externalCacheDir?.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        reportDao.deleteAllReports()
    }

    fun deleteFileFromUri(context: android.content.Context, uriStr: String) {
        if (uriStr.isBlank() || uriStr.startsWith("dummy_")) return
        try {
            val uri = android.net.Uri.parse(uriStr)
            if (uri != null) {
                if (uri.scheme == "content") {
                    try {
                        context.contentResolver.delete(uri, null, null)
                    } catch (e: Exception) {
                        // Ignore content resolver deletion error
                    }

                    val lastSegment = uri.lastPathSegment
                    if (!lastSegment.isNullOrEmpty()) {
                        val potentialDirs = listOf(
                            java.io.File(context.cacheDir, "images"),
                            java.io.File(context.cacheDir, "reports"),
                            java.io.File(context.cacheDir, "recordings"),
                            java.io.File(context.cacheDir, "compressed_media"),
                            java.io.File(context.filesDir, "images"),
                            java.io.File(context.filesDir, "reports"),
                            java.io.File(context.filesDir, "recordings"),
                            context.externalCacheDir
                        )
                        potentialDirs.forEach { dir ->
                            if (dir != null && dir.exists()) {
                                val targetFile = java.io.File(dir, lastSegment)
                                if (targetFile.exists()) {
                                    targetFile.delete()
                                }
                            }
                        }
                    }
                }

                val path = uri.path
                if (!path.isNullOrEmpty()) {
                    val file = java.io.File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }

            val directFile = java.io.File(uriStr)
            if (directFile.exists()) {
                directFile.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
