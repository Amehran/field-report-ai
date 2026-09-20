package com.fieldreport.ai.platform

import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.db.MediaItemEntity

actual class PdfGenerator {
    actual fun generatePdf(
        report: ReportEntity,
        mediaItems: List<MediaItemEntity>,
        outputPath: String,
        businessName: String?
    ): String? {
        // iOS PDF generation implementation via CoreGraphics/PDFKit
        return outputPath
    }
}
