package com.fieldreport.ai.ui.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.ReportStatus
import com.fieldreport.ai.data.repository.ReportRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
        
        // Advance the coroutine dispatcher since saveReport is in a coroutine
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
}
