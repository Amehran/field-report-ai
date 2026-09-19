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

        // Work Completed (Legacy Fallback)
        val workItems = report.workCompletedJson?.split("||")?.filter { it.isNotBlank() } ?: emptyList()
        val findingsItems = report.findingsJson?.split("||")?.filter { it.isNotBlank() } ?: emptyList()
        val recItems = report.recommendationsJson?.split("||")?.filter { it.isNotBlank() } ?: emptyList()

        if (!report.initialStatus.isNullOrBlank()) {
            canvas.drawText("INITIAL STATUS & PROBLEM OBSERVED", 36f, y, sectionTitlePaint)
            y += 16f
            canvas.drawText(report.initialStatus, 36f, y, bodyPaint)
            y += 24f
        }

        val resSteps = report.resolutionStepsJson?.split("||")?.filter { it.isNotBlank() } ?: emptyList()
        if (resSteps.isNotEmpty()) {
            canvas.drawText("WORK EXECUTED & RESOLUTION", 36f, y, sectionTitlePaint)
            y += 16f
            for (item in resSteps) {
                canvas.drawText("• $item", 44f, y, bodyPaint)
                y += 14f
            }
            y += 12f
        } else if (workItems.isNotEmpty()) { // Fallback
            canvas.drawText("WORK COMPLETED", 36f, y, sectionTitlePaint)
            y += 16f
            for (item in workItems) {
                canvas.drawText("• $item", 44f, y, bodyPaint)
                y += 14f
            }
            y += 12f
        }

        if (!report.currentOperationalState.isNullOrBlank()) {
            canvas.drawText("CURRENT OPERATIONAL STATE", 36f, y, sectionTitlePaint)
            y += 16f
            canvas.drawText(report.currentOperationalState, 36f, y, bodyPaint)
            y += 24f
        } else if (findingsItems.isNotEmpty() || recItems.isNotEmpty()) { // Fallback
            if (findingsItems.isNotEmpty()) {
                canvas.drawText("FINDINGS & OBSERVATIONS", 36f, y, sectionTitlePaint)
                y += 16f
                for (item in findingsItems) {
                    canvas.drawText("• $item", 44f, y, bodyPaint)
                    y += 14f
                }
                y += 12f
            }
            if (recItems.isNotEmpty()) {
                canvas.drawText("RECOMMENDED NEXT STEPS", 36f, y, sectionTitlePaint)
                y += 16f
                for (item in recItems) {
                    canvas.drawText("• $item", 44f, y, bodyPaint)
                    y += 14f
                }
                y += 12f
            }
        }

        // Pricing Block
        val hasPricing = report.laborCost != null || report.partsCost != null || report.totalCost != null
        if (hasPricing) {
            y += 8f
            canvas.drawText("JOB CHARGES", 36f, y, sectionTitlePaint)
            y += 16f
            
            // Draw a simple box
            val tablePaint = Paint().apply { color = Color.parseColor("#F8FAFC"); style = Paint.Style.FILL }
            val borderPaint = Paint().apply { color = Color.parseColor("#E2E8F0"); style = Paint.Style.STROKE; strokeWidth = 1f }
            canvas.drawRect(36f, y, 300f, y + 60f, tablePaint)
            canvas.drawRect(36f, y, 300f, y + 60f, borderPaint)
            
            var tableY = y + 16f
            if (report.laborCost != null) {
                canvas.drawText("Labor:", 44f, tableY, bodyPaint)
                canvas.drawText(String.format("$%.2f", report.laborCost), 240f, tableY, bodyPaint)
                tableY += 14f
            }
            if (report.partsCost != null) {
                canvas.drawText("Parts:", 44f, tableY, bodyPaint)
                canvas.drawText(String.format("$%.2f", report.partsCost), 240f, tableY, bodyPaint)
                tableY += 14f
            }
            if (report.totalCost != null) {
                canvas.drawLine(44f, tableY - 4f, 292f, tableY - 4f, borderPaint)
                val boldPaint = Paint(bodyPaint).apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
                canvas.drawText("Total:", 44f, tableY + 10f, boldPaint)
                canvas.drawText(String.format("$%.2f", report.totalCost), 240f, tableY + 10f, boldPaint)
            }
            y += 76f
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
