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

        // Technician & Customer Header
        val techName = report.technicianName.orEmpty().ifBlank { "Lead Service Technician" }
        canvas.drawText("Technician: $techName", 36f, y, subtitlePaint)
        y += 16f
        canvas.drawText("${report.customerName} • ${report.jobTitle}", 36f, y, titlePaint)
        y += 16f
        val fullDateStr = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.US).format(Date(report.createdAt))
        canvas.drawText("Date: $fullDateStr", 36f, y, subtitlePaint)
        y += 20f

        // Divider
        val dividerPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            strokeWidth = 1f
        }
        canvas.drawLine(36f, y, 576f, y, dividerPaint)
        y += 20f

        // ISSUE SECTION
        canvas.drawText("ISSUE", 36f, y, sectionTitlePaint)
        y += 16f
        val issueStr = report.initialStatus.orEmpty().ifBlank { report.findingsJson?.replace("||", "\n• ").orEmpty().ifBlank { "Primary issue identified during initial inspection." } }
        canvas.drawText(issueStr, 36f, y, bodyPaint)
        y += 24f

        // SERVICE SECTION (Work Done & Costs)
        canvas.drawText("SERVICE", 36f, y, sectionTitlePaint)
        y += 16f
        canvas.drawText("What Work Done:", 36f, y, Paint(bodyPaint).apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) })
        y += 14f
        val workDoneStr = report.resolutionStepsJson?.replace("||", "\n• ").orEmpty().ifBlank { report.workCompletedJson?.replace("||", "\n• ").orEmpty().ifBlank { report.typedNotes.orEmpty().ifBlank { report.rawTranscript.orEmpty().ifBlank { "Executed primary repair and testing procedures." } } } }
        canvas.drawText(workDoneStr, 36f, y, bodyPaint)
        y += 24f

        // Pricing Block
        val hasPricing = report.laborCost != null || report.partsCost != null || report.totalCost != null
        if (hasPricing) {
            canvas.drawText("Cost Breakdown:", 36f, y, Paint(bodyPaint).apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) })
            y += 14f
            
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

        // COMMENTS SECTION
        val comments = report.technicianComments
        if (!comments.isNullOrBlank()) {
            canvas.drawText("COMMENTS", 36f, y, sectionTitlePaint)
            y += 16f
            canvas.drawText(comments, 36f, y, bodyPaint)
            y += 24f
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
