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

    @Suppress("UNUSED_PARAMETER")
    fun generatePdf(
        context: Context,
        report: ReportEntity,
        mediaItems: List<MediaItemEntity> = emptyList(),
        businessName: String = "NORTHLINE HOME SERVICES",
        logoUri: String? = null,
        signatureUri: String? = null
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

        // Draw Company Logo if provided
        val logoBitmap = loadBitmap(context, logoUri)
        val textStartX = if (logoBitmap != null) {
            val logoWidth = 48f
            val logoHeight = 48f
            val logoRect = RectF(36f, 11f, 36f + logoWidth, 11f + logoHeight)
            canvas.drawBitmap(logoBitmap, null, logoRect, null)
            96f
        } else {
            36f
        }

        val headerTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val headerTitle = businessName.ifBlank { "NORTHLINE HOME SERVICES" }.uppercase()
        canvas.drawText(headerTitle, textStartX, 40f, headerTextPaint)
        
        val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(report.createdAt))
        val headerSubPaint = Paint().apply {
            color = Color.parseColor("#CCFBF1")
            textSize = 10f
            isAntiAlias = true
        }
        canvas.drawText("Job Completion Report • $dateStr", textStartX, 56f, headerSubPaint)

        y = 95f

        // Customer & Job Header
        val customerJobTitle = "Customer: ${report.customerName}\nJob title: ${report.jobTitle}"
        y = drawWrappedText(canvas, customerJobTitle, 36f, y, 540f, titlePaint, lineSpacing = 4f)
        y += 8f

        // Divider
        val dividerPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            strokeWidth = 1f
        }
        canvas.drawLine(36f, y, 576f, y, dividerPaint)
        y += 20f

        // ISSUE SECTION
        canvas.drawText("ISSUE:", 36f, y, sectionTitlePaint)
        y += 16f
        val rawIssue = report.initialStatus.orEmpty().ifBlank { report.findingsJson.orEmpty().ifBlank { "Primary issue identified during initial inspection." } }
        val issueStr = rawIssue.replace("||", "\n").replace("• ", "").replace("•", "")
        y = drawWrappedText(canvas, issueStr, 36f, y, 540f, bodyPaint, lineSpacing = 4f)
        y += 12f

        // SERVICE SECTION (Work Done & Costs)
        canvas.drawText("SERVICE:", 36f, y, sectionTitlePaint)
        y += 16f
        val rawWorkDone = report.resolutionStepsJson.orEmpty().ifBlank { report.workCompletedJson.orEmpty().ifBlank { report.typedNotes.orEmpty().ifBlank { report.rawTranscript.orEmpty().ifBlank { "Executed primary repair and testing procedures." } } } }
        val workDoneStr = rawWorkDone.replace("||", "\n").replace("• ", "").replace("•", "")
        y = drawWrappedText(canvas, workDoneStr, 36f, y, 540f, bodyPaint, lineSpacing = 4f)
        y += 12f

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
            canvas.drawText("COMMENTS:", 36f, y, sectionTitlePaint)
            y += 16f
            y = drawWrappedText(canvas, comments, 36f, y, 540f, bodyPaint, lineSpacing = 4f)
            y += 12f
        }

        // Technician Signature Section
        val techName = report.technicianName?.takeIf { it.isNotBlank() }
        val sigStartY = maxOf(y + 20f, 670f)

        val sigLabelPaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val sigScriptPaint = Paint().apply {
            color = Color.parseColor("#0F766E")
            textSize = 15f
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }

        val sigLinePaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            strokeWidth = 1f
            isAntiAlias = true
        }

        canvas.drawText("TECHNICIAN SIGNATURE", 36f, sigStartY, sigLabelPaint)

        val sigBitmap = loadBitmap(context, signatureUri)
        if (sigBitmap != null) {
            val sigRect = RectF(36f, sigStartY + 4f, 200f, sigStartY + 44f)
            canvas.drawBitmap(sigBitmap, null, sigRect, null)
        } else if (techName != null) {
            canvas.drawText(techName, 36f, sigStartY + 20f, sigScriptPaint)
        }

        val lineY = sigStartY + 46f
        canvas.drawLine(36f, lineY, 240f, lineY, sigLinePaint)

        // Footer Disclaimer
        val footerY = 760f
        canvas.drawLine(36f, footerY - 12f, 576f, footerY - 12f, dividerPaint)
        drawWrappedText(
            canvas = canvas,
            text = "This report is a summary prepared by the service professional and should be reviewed for accuracy before relying on it.",
            x = 36f,
            startY = footerY,
            maxWidth = 540f,
            paint = disclaimerPaint,
            lineSpacing = 3f
        )

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

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        paint: Paint,
        lineSpacing: Float = 4f
    ): Float {
        var y = startY
        val lineHeight = paint.textSize + lineSpacing
        val paragraphs = text.split("\n")

        for (paragraph in paragraphs) {
            if (paragraph.isEmpty()) {
                y += lineHeight
                continue
            }
            val words = paragraph.split(" ")
            var currentLine = ""

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (paint.measureText(testLine) <= maxWidth) {
                    currentLine = testLine
                } else {
                    if (currentLine.isNotEmpty()) {
                        canvas.drawText(currentLine, x, y, paint)
                        y += lineHeight
                    }
                    currentLine = word
                }
            }
            if (currentLine.isNotEmpty()) {
                canvas.drawText(currentLine, x, y, paint)
                y += lineHeight
            }
        }
        return y
    }

    private fun loadBitmap(context: Context, uriStr: String?): Bitmap? {
        if (uriStr.isNullOrBlank()) return null
        return try {
            val uri = android.net.Uri.parse(uriStr)
            if (uri.scheme == "content" || uri.scheme == "file") {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } else {
                BitmapFactory.decodeFile(uriStr)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
