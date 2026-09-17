package com.fieldreport.ai.data.repository

import com.fieldreport.ai.data.db.ReportDao
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.ReportStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ReportRepositoryTest {

    private lateinit var mockDao: ReportDao
    private lateinit var repository: ReportRepository

    @Before
    fun setup() {
        mockDao = mockk(relaxed = true)
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
    fun `approveReport updates report status to APPROVED`() = runTest {
        val reportId = "test-123"
        val existingReport = ReportEntity(
            id = reportId,
            userId = "user",
            status = ReportStatus.NEEDS_REVIEW,
            customerName = "Jane Doe",
            jobTitle = "Plumbing"
        )

        // Mock the dao to return the existing report when requested
        coEvery { mockDao.getReportById(reportId) } returns existingReport

        repository.approveReport(reportId)

        // Verify that updateReport was called with a report that has status = APPROVED
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
