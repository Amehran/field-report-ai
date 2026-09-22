package com.fieldreport.ai.network

import com.fieldreport.ai.model.GenerateReportRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class CloudRunApiClientTest {

    @Test
    fun testClientInitialization() {
        val client = CloudRunApiClient()
        assertNotNull(client)
    }

    @Test
    fun testGenerateReportRequestModel() {
        val req = GenerateReportRequest(
            title = "Test Inspection",
            jobSite = "Building A",
            inspectorName = "Alex",
            notes = listOf("Concrete checked", "Safety signs verified")
        )
        assertNotNull(req)
    }
}
