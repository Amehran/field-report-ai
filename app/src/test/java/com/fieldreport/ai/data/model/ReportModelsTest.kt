package com.fieldreport.ai.data.model

import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportModelsTest {

    @Test
    fun reportDraftData_defaultValues() {
        val draft = ReportDraftData()
        assertEquals("", draft.customerSummary)
        assertTrue(draft.workCompleted.isEmpty())
        assertTrue(draft.findings.isEmpty())
        assertTrue(draft.recommendations.isEmpty())
        assertTrue(draft.uncertainties.isEmpty())
    }

    @Test
    fun reportDraftData_customValues() {
        val draft = ReportDraftData(
            customerSummary = "Summary test",
            workCompleted = listOf("Task 1", "Task 2"),
            findings = listOf("Finding 1"),
            recommendations = listOf("Rec 1"),
            uncertainties = listOf("Uncertainty 1")
        )
        assertEquals("Summary test", draft.customerSummary)
        assertEquals(2, draft.workCompleted.size)
        assertEquals("Task 1", draft.workCompleted[0])
        assertEquals("Finding 1", draft.findings[0])
        assertEquals("Rec 1", draft.recommendations[0])
        assertEquals("Uncertainty 1", draft.uncertainties[0])
    }

    @Test
    fun localMediaItem_instantiation() {
        val item = LocalMediaItem(
            id = "media-1",
            reportId = "report-1",
            type = MediaType.PHOTO,
            label = PhotoLabel.BEFORE,
            localUri = "file:///data/photo.jpg",
            storagePath = "gs://bucket/photo.jpg",
            sortOrder = 1
        )
        assertEquals("media-1", item.id)
        assertEquals("report-1", item.reportId)
        assertEquals(MediaType.PHOTO, item.type)
        assertEquals(PhotoLabel.BEFORE, item.label)
        assertEquals("file:///data/photo.jpg", item.localUri)
        assertEquals("gs://bucket/photo.jpg", item.storagePath)
        assertEquals(1, item.sortOrder)
    }

    @Test
    fun reportEntity_defaultTimestamps() {
        val entity = ReportEntity(
            id = "rep-1",
            userId = "user-1",
            status = ReportStatus.DRAFT,
            customerName = "Acme Corp",
            jobTitle = "HVAC Repair"
        )
        assertEquals("rep-1", entity.id)
        assertEquals("user-1", entity.userId)
        assertEquals(ReportStatus.DRAFT, entity.status)
        assertEquals("Acme Corp", entity.customerName)
        assertEquals("HVAC Repair", entity.jobTitle)
        assertNull(entity.address)
        assertNull(entity.referenceNumber)
        assertNull(entity.pdfLocalPath)
        assertTrue(entity.createdAt > 0)
        assertTrue(entity.updatedAt > 0)
    }

    @Test
    fun mediaItemEntity_properties() {
        val entity = MediaItemEntity(
            id = "m-1",
            reportId = "rep-1",
            type = MediaType.PHOTO,
            label = PhotoLabel.AFTER,
            localUri = "file:///path.jpg",
            isUploaded = true
        )
        assertEquals("m-1", entity.id)
        assertEquals("rep-1", entity.reportId)
        assertEquals(MediaType.PHOTO, entity.type)
        assertEquals(PhotoLabel.AFTER, entity.label)
        assertEquals("file:///path.jpg", entity.localUri)
        assertNull(entity.storagePath)
        assertEquals(0, entity.sortOrder)
        assertTrue(entity.isUploaded)
    }
}
