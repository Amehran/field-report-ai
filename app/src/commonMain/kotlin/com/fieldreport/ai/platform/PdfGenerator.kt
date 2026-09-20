package com.fieldreport.ai.platform

import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.db.MediaItemEntity

expect class PdfGenerator {
    fun generatePdf(
        report: ReportEntity,
        mediaItems: List<MediaItemEntity>,
        outputPath: String,
        businessName: String? = null
    ): String?
}
