package com.fieldreport.ai.platform

import android.content.Context
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.pdf.PdfReportGenerator

actual class PdfGenerator(private val context: Context) {
    actual fun generatePdf(
        report: ReportEntity,
        mediaItems: List<MediaItemEntity>,
        outputPath: String,
        businessName: String?
    ): String? {
        val generator = PdfReportGenerator(context)
        val file = java.io.File(outputPath)
        return generator.generateReportPdf(report, mediaItems, file, businessName)?.absolutePath
    }
}
