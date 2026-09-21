package com.fieldreport.ai.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.ReportStatus
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.OutputStream

class PdfReportGeneratorTest {

    private val mockContext = mockk<Context>(relaxed = true)
    private val mockPdfDocument = mockk<PdfDocument>(relaxed = true)
    private val mockPage = mockk<PdfDocument.Page>(relaxed = true)
    private val mockCanvas = mockk<Canvas>(relaxed = true)
    private val tempDir = File(System.getProperty("java.io.tmpdir"), "pdf_test_cache")

    @Before
    fun setUp() {
        tempDir.mkdirs()
        every { mockContext.cacheDir } returns tempDir
        mockkConstructor(PdfDocument::class)
        every { anyConstructed<PdfDocument>().startPage(any()) } returns mockPage
        every { mockPage.canvas } returns mockCanvas
        every { anyConstructed<PdfDocument>().writeTo(any<OutputStream>()) } answers { }
        mockkStatic(Color::class)
        every { Color.parseColor(any()) } returns 0
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
        unmockkAll()
    }

    @Test
    fun `generatePdf builds pdf file with report data`() {
        val report = ReportEntity(
            id = "rep-999",
            userId = "user-1",
            status = ReportStatus.APPROVED,
            customerName = "Jane Smith",
            jobTitle = "HVAC Maintenance",
            address = "123 Main St",
            customerSummary = "Replaced air filter and checked pressure.",
            workCompletedJson = "Filter replacement||Pressure test",
            findingsJson = "Low pressure detected",
            recommendationsJson = "Check back in 30 days"
        )

        val file = PdfReportGenerator.generatePdf(mockContext, report, emptyList())

        assertNotNull(file)
        verify { mockCanvas.drawText("Customer: Jane Smith", 36f, any(), any()) }
        verify { mockCanvas.drawText("Job title: HVAC Maintenance", 36f, any(), any()) }
        verify { mockCanvas.drawText("Low pressure detected", 36f, any(), any()) }
        verify { mockCanvas.drawText("Filter replacement", 36f, any(), any()) }
    }

    @Test
    fun `generatePdf handles blank optional fields gracefully`() {
        val report = ReportEntity(
            id = "rep-000",
            userId = "user-1",
            status = ReportStatus.NEEDS_REVIEW,
            customerName = "John Doe",
            jobTitle = "General Repair",
            customerSummary = null,
            workCompletedJson = null,
            findingsJson = null,
            recommendationsJson = null
        )

        val file = PdfReportGenerator.generatePdf(mockContext, report, emptyList(), "CUSTOM BRAND")

        assertNotNull(file)
        assertEquals("Report_rep-000.pdf", file.name)
        verify { mockCanvas.drawText("CUSTOM BRAND", 36f, 40f, any()) }
    }

    @Test
    fun `generatePdf renders cost breakdown and comments when provided`() {
        val report = ReportEntity(
            id = "rep-cost",
            userId = "user-1",
            status = ReportStatus.APPROVED,
            customerName = "Cost Customer",
            jobTitle = "Cost Service",
            laborCost = 100.0,
            partsCost = 50.0,
            totalCost = 150.0,
            technicianComments = "All tasks completed according to specifications."
        )

        val file = PdfReportGenerator.generatePdf(mockContext, report, emptyList())

        assertNotNull(file)
        assertEquals("Report_rep-cost.pdf", file.name)
        verify { mockCanvas.drawText("Cost Breakdown:", 36f, any(), any()) }
        verify { mockCanvas.drawText("Labor:", 44f, any(), any()) }
        verify { mockCanvas.drawText("Parts:", 44f, any(), any()) }
        verify { mockCanvas.drawText("Total:", 44f, any(), any()) }
        verify { mockCanvas.drawText("COMMENTS:", 36f, any(), any()) }
        verify { mockCanvas.drawText("All tasks completed according to specifications.", 36f, any(), any()) }
    }

    @Test
    fun `generatePdf renders technician signature when technicianName is provided`() {
        val report = ReportEntity(
            id = "rep-sig",
            userId = "user-1",
            status = ReportStatus.APPROVED,
            customerName = "Signed Customer",
            jobTitle = "Service",
            technicianName = "Alex Smith"
        )

        val file = PdfReportGenerator.generatePdf(mockContext, report, emptyList())

        assertNotNull(file)
        verify { mockCanvas.drawText("TECHNICIAN SIGNATURE", 36f, any(), any()) }
        verify { mockCanvas.drawText("Alex Smith", 36f, any(), any()) }
    }
}
