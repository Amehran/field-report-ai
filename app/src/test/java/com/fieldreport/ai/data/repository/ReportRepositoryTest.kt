package com.fieldreport.ai.data.repository

import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportDao
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.MediaType
import com.fieldreport.ai.data.model.PhotoLabel
import com.fieldreport.ai.data.model.ReportStatus
import io.mockk.*
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
        val mockContext = mockk<android.content.Context>(relaxed = true)
        val item = MediaItemEntity(
            id = "media-1",
            reportId = "test-123",
            type = MediaType.PHOTO,
            label = PhotoLabel.BEFORE,
            localUri = "content://media/1"
        )

        repository.deleteMediaItem(mockContext, item)

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
    fun `generateLocalMockDraft handles HVAC job title`() = runTest {
        val reportId = "test-hvac"
        val existingReport = ReportEntity(id = reportId, userId = "user", status = ReportStatus.DRAFT, customerName = "John", jobTitle = "HVAC Thermostat")
        coEvery { mockDao.getReportById(reportId) } returns existingReport

        repository.generateLocalMockDraft(reportId, "Calibrated sensors")

        coVerify(exactly = 1) {
            mockDao.updateReport(match {
                it.id == reportId && it.workCompletedJson?.contains("thermostat") == true
            })
        }
    }

    @Test
    fun `generateLocalMockDraft handles Plumbing job title`() = runTest {
        val reportId = "test-plumbing"
        val existingReport = ReportEntity(id = reportId, userId = "user", status = ReportStatus.DRAFT, customerName = "Mary", jobTitle = "Pipe Leak Repair")
        coEvery { mockDao.getReportById(reportId) } returns existingReport

        repository.generateLocalMockDraft(reportId, "Tightened valve")

        coVerify(exactly = 1) {
            mockDao.updateReport(match {
                it.id == reportId && it.workCompletedJson?.contains("supply lines") == true
            })
        }
    }

    @Test
    fun `generateLocalMockDraft handles Electrical job title`() = runTest {
        val reportId = "test-electrical"
        val existingReport = ReportEntity(id = reportId, userId = "user", status = ReportStatus.DRAFT, customerName = "Bob", jobTitle = "Panel Outlet Repair")
        coEvery { mockDao.getReportById(reportId) } returns existingReport

        repository.generateLocalMockDraft(reportId, "Replaced breaker")

        coVerify(exactly = 1) {
            mockDao.updateReport(match {
                it.id == reportId && it.workCompletedJson?.contains("continuity") == true
            })
        }
    }

    @Test
    fun `setAudioRecording calls dao updateAudioLocalUri`() = runTest {
        repository.setAudioRecording("rep-1", "file:///audio.m4a")

        coVerify(exactly = 1) { mockDao.updateAudioLocalUri("rep-1", "file:///audio.m4a") }
    }

    @Test
    fun `deleteReport calls dao deleteReport`() = runTest {
        repository.deleteReport("rep-1")

        coVerify(exactly = 1) { mockDao.deleteReport("rep-1") }
    }

    @Test
    fun `deleteAllReports calls dao deleteAllReports`() = runTest {
        repository.deleteAllReports()

        coVerify(exactly = 1) { mockDao.deleteAllReports() }
    }

    @Test
    fun `deleteReportWithFiles deletes report and files`() = runTest {
        mockkStatic(android.net.Uri::class)
        val mockUri = mockk<android.net.Uri>(relaxed = true)
        every { mockUri.scheme } returns "file"
        every { mockUri.path } returns "/data/audio.m4a"
        every { android.net.Uri.parse(any()) } returns mockUri

        val mockContext = mockk<android.content.Context>(relaxed = true)
        every { mockContext.cacheDir } returns java.io.File(System.getProperty("java.io.tmpdir") ?: "/tmp")

        val report = ReportEntity(id = "rep-files", userId = "user", status = ReportStatus.APPROVED, customerName = "A", jobTitle = "B", audioLocalUri = "file:///data/audio.m4a")
        val media = listOf(MediaItemEntity(id = "m1", reportId = "rep-files", type = MediaType.PHOTO, label = PhotoLabel.BEFORE, localUri = "file:///data/img.jpg"))

        coEvery { mockDao.getReportById("rep-files") } returns report
        coEvery { mockDao.getMediaItemsForReport("rep-files") } returns media

        repository.deleteReportWithFiles(mockContext, "rep-files")

        coVerify { mockDao.deleteReport("rep-files") }
    }

    @Test
    fun `deleteAllReportsWithFiles iterates and deletes all reports`() = runTest {
        mockkStatic(android.net.Uri::class)
        val mockUri = mockk<android.net.Uri>(relaxed = true)
        every { mockUri.scheme } returns "file"
        every { mockUri.path } returns "/data/audio.m4a"
        every { android.net.Uri.parse(any()) } returns mockUri

        val mockContext = mockk<android.content.Context>(relaxed = true)
        every { mockContext.cacheDir } returns java.io.File(System.getProperty("java.io.tmpdir") ?: "/tmp")

        val reports = listOf(ReportEntity(id = "rep-1", userId = "user", status = ReportStatus.APPROVED, customerName = "A", jobTitle = "B"))
        coEvery { mockDao.getAllReportsList() } returns reports
        coEvery { mockDao.getReportById("rep-1") } returns reports[0]
        coEvery { mockDao.getMediaItemsForReport("rep-1") } returns emptyList()

        repository.deleteAllReportsWithFiles(mockContext)

        coVerify { mockDao.deleteAllReports() }
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
