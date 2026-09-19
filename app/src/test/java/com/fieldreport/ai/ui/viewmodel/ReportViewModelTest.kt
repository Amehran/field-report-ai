package com.fieldreport.ai.ui.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.MediaType
import com.fieldreport.ai.data.model.PhotoLabel
import com.fieldreport.ai.data.model.ReportStatus
import com.fieldreport.ai.data.repository.ReportRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ReportViewModel
    private val mockApp = mockk<Application>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock the repository constructor so the ViewModel gets a mock
        mockkConstructor(ReportRepository::class)
        
        val mockFlow = MutableStateFlow<List<ReportEntity>>(emptyList())
        every { anyConstructed<ReportRepository>().allReports } returns mockFlow

        viewModel = ReportViewModel(mockApp)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `startNewReport generates a new report and sets current id`() = runTest {
        coEvery { anyConstructed<ReportRepository>().saveReport(any()) } just Runs

        viewModel.startNewReport("John Doe", "Repair")
        
        advanceUntilIdle()

        val id = viewModel.currentReportId.value
        assertNotNull(id)
        
        coVerify { 
            anyConstructed<ReportRepository>().saveReport(match {
                it.customerName == "John Doe" && 
                it.jobTitle == "Repair" && 
                it.status == ReportStatus.DRAFT
            })
        }
    }

    @Test
    fun `setCurrentReportId updates the current report id flow`() {
        viewModel.setCurrentReportId("report-123")
        assertEquals("report-123", viewModel.currentReportId.value)
    }

    @Test
    fun `addPhoto saves a new media item to repository`() = runTest {
        coEvery { anyConstructed<ReportRepository>().addMediaItem(any()) } just Runs
        viewModel.setCurrentReportId("report-123")

        viewModel.addPhoto("content://media/photo1", PhotoLabel.BEFORE)
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().addMediaItem(match {
                it.reportId == "report-123" &&
                it.localUri == "content://media/photo1" &&
                it.label == PhotoLabel.BEFORE &&
                it.type == MediaType.PHOTO
            })
        }
    }

    @Test
    fun `togglePhotoLabel rotates photo labels correctly`() = runTest {
        coEvery { anyConstructed<ReportRepository>().addMediaItem(any()) } just Runs

        val itemBefore = MediaItemEntity(
            id = "m-1",
            reportId = "rep-1",
            type = MediaType.PHOTO,
            label = PhotoLabel.BEFORE,
            localUri = "uri"
        )
        viewModel.togglePhotoLabel(itemBefore)
        advanceUntilIdle()
        coVerify { anyConstructed<ReportRepository>().addMediaItem(match { it.label == PhotoLabel.AFTER }) }

        val itemAfter = itemBefore.copy(label = PhotoLabel.AFTER)
        viewModel.togglePhotoLabel(itemAfter)
        advanceUntilIdle()
        coVerify { anyConstructed<ReportRepository>().addMediaItem(match { it.label == PhotoLabel.GENERAL }) }

        val itemGeneral = itemBefore.copy(label = PhotoLabel.GENERAL)
        viewModel.togglePhotoLabel(itemGeneral)
        advanceUntilIdle()
        coVerify { anyConstructed<ReportRepository>().addMediaItem(match { it.label == PhotoLabel.BEFORE }) }
    }

    @Test
    fun `updateWorkCompleted updates report with joined string`() = runTest {
        val report = ReportEntity(
            id = "rep-1",
            userId = "user",
            status = ReportStatus.NEEDS_REVIEW,
            customerName = "Acme",
            jobTitle = "HVAC"
        )
        val reportFlow = MutableStateFlow<ReportEntity?>(report)
        every { anyConstructed<ReportRepository>().getReportById("rep-1") } returns reportFlow
        coEvery { anyConstructed<ReportRepository>().updateReport(any()) } just Runs

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentReport.collect()
        }

        viewModel.setCurrentReportId("rep-1")
        advanceUntilIdle()

        viewModel.updateWorkCompleted(listOf("Step 1", "Step 2"))
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().updateReport(match {
                it.workCompletedJson == "Step 1||Step 2"
            })
        }
    }

    @Test
    fun `updateFindings updates report findingsJson`() = runTest {
        val report = ReportEntity(
            id = "rep-1",
            userId = "user",
            status = ReportStatus.NEEDS_REVIEW,
            customerName = "Acme",
            jobTitle = "HVAC"
        )
        val reportFlow = MutableStateFlow<ReportEntity?>(report)
        every { anyConstructed<ReportRepository>().getReportById("rep-1") } returns reportFlow
        coEvery { anyConstructed<ReportRepository>().updateReport(any()) } just Runs

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentReport.collect()
        }

        viewModel.setCurrentReportId("rep-1")
        advanceUntilIdle()

        viewModel.updateFindings(listOf("Found leak near pipe"))
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().updateReport(match {
                it.findingsJson == "Found leak near pipe"
            })
        }
    }

    @Test
    fun `updateRecommendations updates report recommendationsJson`() = runTest {
        val report = ReportEntity(
            id = "rep-1",
            userId = "user",
            status = ReportStatus.NEEDS_REVIEW,
            customerName = "Acme",
            jobTitle = "HVAC"
        )
        val reportFlow = MutableStateFlow<ReportEntity?>(report)
        every { anyConstructed<ReportRepository>().getReportById("rep-1") } returns reportFlow
        coEvery { anyConstructed<ReportRepository>().updateReport(any()) } just Runs

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentReport.collect()
        }

        viewModel.setCurrentReportId("rep-1")
        advanceUntilIdle()

        viewModel.updateRecommendations(listOf("Replace valve in 6 months"))
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().updateReport(match {
                it.recommendationsJson == "Replace valve in 6 months"
            })
        }
    }

    @Test
    fun `updateReportHeader updates customerName and jobTitle on current report`() = runTest {
        val report = ReportEntity(
            id = "rep-1",
            userId = "user",
            status = ReportStatus.DRAFT,
            customerName = "Old Customer",
            jobTitle = "Old Repair"
        )
        val reportFlow = MutableStateFlow<ReportEntity?>(report)
        every { anyConstructed<ReportRepository>().getReportById("rep-1") } returns reportFlow
        coEvery { anyConstructed<ReportRepository>().updateReport(any()) } just Runs

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentReport.collect()
        }

        viewModel.setCurrentReportId("rep-1")
        advanceUntilIdle()

        viewModel.updateReportHeader("New Customer", "Thermostat Repair")
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().updateReport(match {
                it.customerName == "New Customer" && it.jobTitle == "Thermostat Repair"
            })
        }
    }
}
