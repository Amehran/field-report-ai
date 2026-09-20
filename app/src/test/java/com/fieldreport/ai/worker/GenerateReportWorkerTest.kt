package com.fieldreport.ai.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import com.fieldreport.ai.data.db.AppDatabase
import com.fieldreport.ai.data.db.ReportDao
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GenerateReportWorkerTest {

    private val mockContext = mockk<Context>(relaxed = true)
    private val mockDb = mockk<AppDatabase>(relaxed = true)
    private val mockDao = mockk<ReportDao>(relaxed = true)

    @Before
    fun setUp() {
        mockkObject(AppDatabase.Companion)
        every { AppDatabase.getDatabase(any()) } returns mockDb
        every { mockDb.reportDao() } returns mockDao
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `doWork returns failure when reportId is missing`() = runTest {
        val worker = TestListenableWorkerBuilder<GenerateReportWorker>(mockContext)
            .setInputData(Data.EMPTY)
            .build()

        val result = worker.doWork()

        assertEquals(Result.failure(), result)
    }

    @Test
    fun `doWork returns failure when report is not found in room db`() = runTest {
        coEvery { mockDao.getReportById("rep-missing") } returns null

        val inputData = Data.Builder().putString("reportId", "rep-missing").build()
        val worker = TestListenableWorkerBuilder<GenerateReportWorker>(mockContext)
            .setInputData(inputData)
            .build()

        val result = worker.doWork()

        assertEquals(Result.failure(), result)
    }

    @Test
    fun `doWork returns failure when user is not authenticated`() = runTest {
        val report = com.fieldreport.ai.data.db.ReportEntity(
            id = "rep-unauth",
            userId = "user",
            status = com.fieldreport.ai.data.model.ReportStatus.DRAFT,
            customerName = "Cust",
            jobTitle = "Job"
        )
        coEvery { mockDao.getReportById("rep-unauth") } returns report
        coEvery { mockDao.getMediaItemsForReport("rep-unauth") } returns emptyList()

        val mockAuth = mockk<com.google.firebase.auth.FirebaseAuth>(relaxed = true)
        every { mockAuth.currentUser } returns null
        mockkStatic(com.google.firebase.auth.FirebaseAuth::class)
        every { com.google.firebase.auth.FirebaseAuth.getInstance() } returns mockAuth

        val inputData = Data.Builder().putString("reportId", "rep-unauth").build()
        val worker = TestListenableWorkerBuilder<GenerateReportWorker>(mockContext)
            .setInputData(inputData)
            .build()

        val result = worker.doWork()

        assertEquals(Result.failure(), result)
    }
}
