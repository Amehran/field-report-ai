package com.fieldreport.ai.pdf

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfReportGenerator {

    fun generatePdf(
        context: Context,
        report: ReportEntity,
        mediaItems: List<MediaItemEntity>,
        businessName: String = "NORTHLINE HOME SERVICES"
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1).create() // Letter size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val primaryPaint = Paint().apply {
            color = Color.parseColor("#0F766E")
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.parseColor("#111827")
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 11f
            isAntiAlias = true
        }

        val sectionTitlePaint = Paint().apply {
            color = Color.parseColor("#0F766E")
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.parseColor("#111827")
            textSize = 10f
            isAntiAlias = true
        }

        val disclaimerPaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 8f
            isAntiAlias = true
        }

        var y: Float

        // Top Header Banner
        canvas.drawRect(0f, 0f, 612f, 70f, primaryPaint)

        val headerTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(businessName.uppercase(), 36f, 40f, headerTextPaint)
        
        val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(report.createdAt))
        val headerSubPaint = Paint().apply {
            color = Color.parseColor("#CCFBF1")
            textSize = 10f
            isAntiAlias = true
        }
        canvas.drawText("Job Completion Report • $dateStr", 36f, 56f, headerSubPaint)

        y = 95f

        // Customer Block
        canvas.drawText(report.customerName, 36f, y, titlePaint)
        y += 16f
        canvas.drawText(report.jobTitle + (if (!report.address.isNullOrBlank()) " • ${report.address}" else ""), 36f, y, subtitlePaint)
        y += 24f

        // Divider
        val dividerPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            strokeWidth = 1f
        }
        canvas.drawLine(36f, y, 576f, y, dividerPaint)
        y += 20f

        // Customer Summary
        val summary = report.customerSummary
        if (!summary.isNullOrBlank()) {
            canvas.drawText("SUMMARY", 36f, y, sectionTitlePaint)
            y += 16f
            canvas.drawText(summary, 36f, y, bodyPaint)
            y += 24f
        }

        // Work Completed
        val workItems = report.workCompletedJson?.split("||")?.filter { it.isNotBlank() } ?: emptyList()
        if (workItems.isNotEmpty()) {
            canvas.drawText("WORK COMPLETED", 36f, y, sectionTitlePaint)
            y += 16f
            for (item in workItems) {
                canvas.drawText("• $item", 44f, y, bodyPaint)
                y += 14f
            }
            y += 12f
        }

        // Findings
        val findingsItems = report.findingsJson?.split("||")?.filter { it.isNotBlank() } ?: emptyList()
        if (findingsItems.isNotEmpty()) {
            canvas.drawText("FINDINGS & OBSERVATIONS", 36f, y, sectionTitlePaint)
            y += 16f
            for (item in findingsItems) {
                canvas.drawText("• $item", 44f, y, bodyPaint)
                y += 14f
            }
            y += 12f
        }

        // Recommendations
        val recItems = report.recommendationsJson?.split("||")?.filter { it.isNotBlank() } ?: emptyList()
        if (recItems.isNotEmpty()) {
            canvas.drawText("RECOMMENDED NEXT STEPS", 36f, y, sectionTitlePaint)
            y += 16f
            for (item in recItems) {
                canvas.drawText("• $item", 44f, y, bodyPaint)
                y += 14f
            }
            y += 12f
        }

        // Footer Disclaimer
        val footerY = 760f
        canvas.drawLine(36f, footerY - 12f, 576f, footerY - 12f, dividerPaint)
        canvas.drawText("This report is a summary prepared by the service professional and should be reviewed for accuracy before relying on it.", 36f, footerY, disclaimerPaint)

        pdfDocument.finishPage(page)

        // Write PDF file
        val pdfDir = File(context.cacheDir, "reports")
        if (!pdfDir.exists()) pdfDir.mkdirs()
        
        val pdfFile = File(pdfDir, "Report_${report.id}.pdf")
        val outputStream = FileOutputStream(pdfFile)
        pdfDocument.writeTo(outputStream)
        outputStream.close()
        pdfDocument.close()

        return pdfFile
    }
}
