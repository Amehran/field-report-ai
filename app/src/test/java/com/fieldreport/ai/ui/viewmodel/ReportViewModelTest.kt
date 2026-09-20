package com.fieldreport.ai.ui.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.MediaType
import com.fieldreport.ai.data.model.PhotoLabel
import com.fieldreport.ai.data.model.ReportStatus
import com.fieldreport.ai.data.repository.ReportRepository
import com.fieldreport.ai.data.repository.SettingsRepository
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
    private val tempDir = java.io.File(System.getProperty("java.io.tmpdir"), "vm_test_cache")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tempDir.mkdirs()
        every { mockApp.filesDir } returns tempDir
        every { mockApp.applicationContext } returns mockApp
        
        // Mock the repository constructors so the ViewModel gets mocks
        mockkConstructor(ReportRepository::class)
        mockkConstructor(SettingsRepository::class)

        coJustRun { anyConstructed<SettingsRepository>().setAiAgentMode(any()) }
        coJustRun { anyConstructed<SettingsRepository>().setBusinessName(any()) }
        coJustRun { anyConstructed<SettingsRepository>().setCurrency(any()) }
        coJustRun { anyConstructed<SettingsRepository>().setTechnicianName(any()) }

        coJustRun { anyConstructed<ReportRepository>().generateLocalMockDraft(any(), any()) }

        val mockFlow = MutableStateFlow<List<ReportEntity>>(emptyList())
        every { anyConstructed<ReportRepository>().allReports } returns mockFlow

        viewModel = ReportViewModel(mockApp)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        tempDir.deleteRecursively()
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
    fun `startNewReport with address saves report with address parameter`() = runTest {
        coEvery { anyConstructed<ReportRepository>().saveReport(any()) } just Runs

        viewModel.startNewReport("Customer X", "Job Y", "123 Main St")
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().saveReport(match {
                it.customerName == "Customer X" && it.jobTitle == "Job Y" && it.address == "123 Main St"
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

    @Test
    fun `updateCosts updates labor, parts, and total cost on current report`() = runTest {
        val report = ReportEntity(
            id = "rep-1",
            userId = "user",
            status = ReportStatus.DRAFT,
            customerName = "Customer",
            jobTitle = "Job"
        )
        val reportFlow = MutableStateFlow<ReportEntity?>(report)
        every { anyConstructed<ReportRepository>().getReportById("rep-1") } returns reportFlow
        coEvery { anyConstructed<ReportRepository>().updateReport(any()) } just Runs

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentReport.collect()
        }

        viewModel.setCurrentReportId("rep-1")
        advanceUntilIdle()

        viewModel.updateCosts(150.0, 75.5, 225.5)
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().updateReport(match {
                it.laborCost == 150.0 && it.partsCost == 75.5 && it.totalCost == 225.5
            })
        }
    }

    @Test
    fun `updateReportReviewData updates issue, work done, technician comments, and technician name`() = runTest {
        val report = ReportEntity(
            id = "rep-1",
            userId = "user",
            status = ReportStatus.NEEDS_REVIEW,
            customerName = "Customer",
            jobTitle = "Job"
        )
        val reportFlow = MutableStateFlow<ReportEntity?>(report)
        every { anyConstructed<ReportRepository>().getReportById("rep-1") } returns reportFlow
        coEvery { anyConstructed<ReportRepository>().updateReport(any()) } just Runs

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentReport.collect()
        }

        viewModel.setCurrentReportId("rep-1")
        advanceUntilIdle()

        viewModel.updateReportReviewData("New Issue", "New Work Done", "Good job", "Tech John")
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().updateReport(match {
                it.initialStatus == "New Issue" &&
                it.resolutionStepsJson == "New Work Done" &&
                it.workCompletedJson == "New Work Done" &&
                it.technicianComments == "Good job" &&
                it.technicianName == "Tech John"
            })
        }
    }

    @Test
    fun `setAudioRecording calls repository setAudioRecording`() = runTest {
        coEvery { anyConstructed<ReportRepository>().setAudioRecording(any(), any()) } just Runs

        viewModel.setCurrentReportId("rep-audio")
        viewModel.setAudioRecording("content://audio/rec.m4a")
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().setAudioRecording("rep-audio", "content://audio/rec.m4a")
        }
    }

    @Test
    fun `deleteReport calls repository deleteReportWithFiles`() = runTest {
        coEvery { anyConstructed<ReportRepository>().deleteReportWithFiles(any(), any()) } just Runs

        viewModel.deleteReport("rep-delete")
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().deleteReportWithFiles(mockApp, "rep-delete")
        }
    }

    @Test
    fun `deleteAllReports calls repository deleteAllReportsWithFiles`() = runTest {
        coEvery { anyConstructed<ReportRepository>().deleteAllReportsWithFiles(any()) } just Runs

        viewModel.deleteAllReports()
        advanceUntilIdle()

        coVerify {
            anyConstructed<ReportRepository>().deleteAllReportsWithFiles(mockApp)
        }
    }

    @Test
    fun `approveReport calls repository approveReport`() = runTest {
        coEvery { anyConstructed<ReportRepository>().approveReport(any()) } just Runs

        viewModel.setCurrentReportId("rep-approve")
        viewModel.approveReport()
        advanceUntilIdle()

        coVerify { anyConstructed<ReportRepository>().approveReport("rep-approve") }
    }

    @Test
    fun `settings setters delegate to settingsRepository`() = runTest {
        viewModel.setAiAgentMode(com.fieldreport.ai.data.model.AiAgentMode.ON_DEVICE)
        viewModel.setBusinessName("New Business")
        viewModel.setCurrency("EUR")
        viewModel.setTechnicianName("Tech Name")
        advanceUntilIdle()

        coVerify { anyConstructed<SettingsRepository>().setAiAgentMode(com.fieldreport.ai.data.model.AiAgentMode.ON_DEVICE) }
        coVerify { anyConstructed<SettingsRepository>().setBusinessName("New Business") }
        coVerify { anyConstructed<SettingsRepository>().setCurrency("EUR") }
        coVerify { anyConstructed<SettingsRepository>().setTechnicianName("Tech Name") }
    }

    @Test
    fun `generateReportDraft handles auth failure with local mock draft fallback`() = runTest {
        val report = ReportEntity(id = "rep-draft-1", userId = "usr_demo", status = ReportStatus.DRAFT, customerName = "Cust A", jobTitle = "Job A")
        val reportFlow = MutableStateFlow<ReportEntity?>(report)
        every { anyConstructed<ReportRepository>().getReportById("rep-draft-1") } returns reportFlow

        val mockAuth = mockk<com.google.firebase.auth.FirebaseAuth>(relaxed = true)
        val mockTask = mockk<com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult>>(relaxed = true)
        every { mockTask.isComplete } returns true
        every { mockTask.isSuccessful } returns false
        every { mockTask.isCanceled } returns false
        every { mockTask.exception } returns Exception("Auth failed")
        every { mockAuth.currentUser } returns null
        every { mockAuth.signInAnonymously() } returns mockTask

        mockkStatic(com.google.firebase.auth.FirebaseAuth::class)
        every { com.google.firebase.auth.FirebaseAuth.getInstance() } returns mockAuth

        viewModel.setCurrentReportId("rep-draft-1")
        advanceUntilIdle()

        viewModel.generateReportDraft("Customer A", "Thermostat", "Notes")
        advanceUntilIdle()

        coVerify { anyConstructed<ReportRepository>().generateLocalMockDraft("rep-draft-1", "Notes") }
    }

    @Test
    fun `generateReportDraft with authenticated user and local media items`() = runTest {
        val report = ReportEntity(
            id = "rep-auth-1",
            userId = "usr_123",
            status = ReportStatus.DRAFT,
            customerName = "Cust Auth",
            jobTitle = "Job Auth",
            audioLocalUri = "content://media/audio.m4a"
        )
        val media = listOf(
            MediaItemEntity(
                id = "m-1",
                reportId = "rep-auth-1",
                type = MediaType.PHOTO,
                label = PhotoLabel.BEFORE,
                localUri = "content://media/photo.jpg",
                isUploaded = false
            )
        )
        val reportFlow = MutableStateFlow<ReportEntity?>(report)
        val mediaFlow = MutableStateFlow<List<MediaItemEntity>>(media)

        every { anyConstructed<ReportRepository>().getReportById("rep-auth-1") } returns reportFlow
        every { anyConstructed<ReportRepository>().getMediaForReport("rep-auth-1") } returns mediaFlow
        coEvery { anyConstructed<ReportRepository>().updateReport(any()) } just Runs
        coEvery { anyConstructed<ReportRepository>().updateMediaStoragePath(any(), any(), any()) } just Runs
        coEvery { anyConstructed<ReportRepository>().updateReportAudioStoragePath(any(), any()) } just Runs
        coEvery { anyConstructed<ReportRepository>().generateLocalMockDraft(any(), any()) } just Runs

        val mockUser = mockk<com.google.firebase.auth.FirebaseUser>(relaxed = true)
        every { mockUser.uid } returns "usr_123"
        val mockTokenTask = mockk<com.google.android.gms.tasks.Task<com.google.firebase.auth.GetTokenResult>>(relaxed = true)
        every { mockTokenTask.isComplete } returns true
        every { mockTokenTask.isSuccessful } returns false
        every { mockTokenTask.exception } returns Exception("Token error")
        every { mockUser.getIdToken(any()) } returns mockTokenTask

        val mockAuth = mockk<com.google.firebase.auth.FirebaseAuth>(relaxed = true)
        every { mockAuth.currentUser } returns mockUser
        mockkStatic(com.google.firebase.auth.FirebaseAuth::class)
        every { com.google.firebase.auth.FirebaseAuth.getInstance() } returns mockAuth

        val mockStorage = mockk<com.google.firebase.storage.FirebaseStorage>(relaxed = true)
        mockkStatic(com.google.firebase.storage.FirebaseStorage::class)
        every { com.google.firebase.storage.FirebaseStorage.getInstance() } returns mockStorage

        val mockStorageRef = mockk<com.google.firebase.storage.StorageReference>(relaxed = true)
        every { mockStorage.reference } returns mockStorageRef
        every { mockStorageRef.child(any()) } returns mockStorageRef
        every { mockStorageRef.bucket } returns "test-bucket"
        every { mockStorageRef.path } returns "/test/path"
        val mockUploadTask = mockk<com.google.firebase.storage.UploadTask>(relaxed = true)
        every { mockUploadTask.isComplete } returns true
        every { mockUploadTask.isSuccessful } returns true
        every { mockStorageRef.putFile(any()) } returns mockUploadTask

        mockkStatic(android.net.Uri::class)
        val mockUri = mockk<android.net.Uri>(relaxed = true)
        every { android.net.Uri.parse(any()) } returns mockUri

        viewModel.setCurrentReportId("rep-auth-1")
        advanceUntilIdle()

        viewModel.generateReportDraft("Cust Auth", "Job Auth", "Some notes")
        advanceUntilIdle()

        coVerify { anyConstructed<ReportRepository>().generateLocalMockDraft("rep-auth-1", "Some notes") }
    }
}
