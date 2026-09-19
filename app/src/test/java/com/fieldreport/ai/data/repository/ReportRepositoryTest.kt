package com.fieldreport.ai.data.repository

import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportDao
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.MediaType
import com.fieldreport.ai.data.model.PhotoLabel
import com.fieldreport.ai.data.model.ReportStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.Runs
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReportRepositoryTest {

    private lateinit var mockDao: ReportDao
    private lateinit var repository: ReportRepository

    @Before
    fun setup() {
        mockDao = mockk(relaxed = true)
        coEvery { mockDao.deleteMediaItem(any()) } just Runs
        repository = ReportRepository(mockDao)
    }

    @Test
    fun `saveReport calls dao insertReport`() = runTest {
        val report = ReportEntity(
            id = "test-123",
            userId = "user",
            status = ReportStatus.DRAFT,
            customerName = "Jane Doe",
            jobTitle = "Plumbing"
        )

        repository.saveReport(report)

        coVerify(exactly = 1) { mockDao.insertReport(report) }
    }

    @Test
    fun `updateReport calls dao updateReport`() = runTest {
        val report = ReportEntity(
            id = "test-123",
            userId = "user",
            status = ReportStatus.GENERATING,
            customerName = "Jane Doe",
            jobTitle = "Plumbing"
        )

        repository.updateReport(report)

        coVerify(exactly = 1) { mockDao.updateReport(report) }
    }

    @Test
    fun `addMediaItem calls dao insertMediaItem`() = runTest {
        val item = MediaItemEntity(
            id = "media-1",
            reportId = "test-123",
            type = MediaType.PHOTO,
            label = PhotoLabel.BEFORE,
            localUri = "content://media/1"
        )

        repository.addMediaItem(item)

        coVerify(exactly = 1) { mockDao.insertMediaItem(item) }
    }

    @Test
    fun `deleteMediaItem calls dao deleteMediaItem`() = runTest {
        val item = MediaItemEntity(
            id = "media-1",
            reportId = "test-123",
            type = MediaType.PHOTO,
            label = PhotoLabel.BEFORE,
            localUri = "content://media/1"
        )

        repository.deleteMediaItem(item)

        coVerify(exactly = 1) { mockDao.deleteMediaItem(item) }
    }

    @Test
    fun `updateMediaStoragePath calls dao updateMediaStoragePath`() = runTest {
        repository.updateMediaStoragePath("media-1", "gs://bucket/path.jpg", true)

        coVerify(exactly = 1) { mockDao.updateMediaStoragePath("media-1", "gs://bucket/path.jpg", true) }
    }

    @Test
    fun `updateReportAudioStoragePath calls dao updateReportAudioStoragePath`() = runTest {
        repository.updateReportAudioStoragePath("rep-1", "gs://bucket/audio.m4a")

        coVerify(exactly = 1) { mockDao.updateReportAudioStoragePath("rep-1", "gs://bucket/audio.m4a") }
    }

    @Test
    fun `generateLocalMockDraft updates report with draft sections and status NEEDS_REVIEW`() = runTest {
        val reportId = "test-123"
        val existingReport = ReportEntity(
            id = reportId,
            userId = "user",
            status = ReportStatus.DRAFT,
            customerName = "Jane Doe",
            jobTitle = "Cabinet Repair"
        )

        coEvery { mockDao.getReportById(reportId) } returns existingReport

        repository.generateLocalMockDraft(reportId, "Replaced 2 hinges")

        coVerify(exactly = 1) {
            mockDao.updateReport(match {
                it.id == reportId &&
                it.status == ReportStatus.NEEDS_REVIEW &&
                it.customerSummary != null &&
                it.workCompletedJson != null &&
                it.findingsJson != null &&
                it.recommendationsJson != null &&
                it.rawTranscript == "Replaced 2 hinges"
            })
        }
    }

    @Test
    fun `generateLocalMockDraft does nothing if report not found`() = runTest {
        coEvery { mockDao.getReportById("missing") } returns null

        repository.generateLocalMockDraft("missing", null)

        coVerify(exactly = 0) { mockDao.updateReport(any()) }
    }

    @Test
    fun `approveReport updates report status to APPROVED`() = runTest {
        val reportId = "test-123"
        val existingReport = ReportEntity(
            id = reportId,
            userId = "user",
            status = ReportStatus.NEEDS_REVIEW,
            customerName = "Jane Doe",
            jobTitle = "Plumbing"
        )

        coEvery { mockDao.getReportById(reportId) } returns existingReport

        repository.approveReport(reportId)

        coVerify(exactly = 1) { 
            mockDao.updateReport(match { it.id == reportId && it.status == ReportStatus.APPROVED }) 
        }
    }

    @Test
    fun `approveReport does nothing if report does not exist`() = runTest {
        val reportId = "missing-123"

        coEvery { mockDao.getReportById(reportId) } returns null

        repository.approveReport(reportId)

        coVerify(exactly = 0) { mockDao.updateReport(any()) }
    }
}
