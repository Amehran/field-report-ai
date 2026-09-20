package com.fieldreport.ai.agent

import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.AiAgentMode
import com.fieldreport.ai.data.model.ReportStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportAgentTest {

    @Test
    fun factory_createsCorrectAgentType() {
        val cloudAgent = ReportAgentFactory.createAgent(AiAgentMode.CLOUD)
        assertTrue(cloudAgent is CloudGeminiAgent)

        val onDeviceAgent = ReportAgentFactory.createAgent(AiAgentMode.ON_DEVICE)
        assertTrue(onDeviceAgent is OnDeviceAgent)
    }

    @Test
    fun cloudAgent_generateDraft_returnsSuccessResult() = runTest {
        val agent = CloudGeminiAgent()
        val report = ReportEntity(
            id = "rep-1",
            userId = "user-1",
            status = ReportStatus.DRAFT,
            customerName = "Customer A",
            jobTitle = "Job A"
        )

        val result = agent.generateDraft(report, emptyList(), "Some notes")

        assertTrue(result.isSuccess)
        assertEquals(report, result.getOrNull())
    }

    @Test
    fun onDeviceAgent_generateDraft_returnsSuccessResult() = runTest {
        val agent = OnDeviceAgent()
        val report = ReportEntity(
            id = "rep-2",
            userId = "user-2",
            status = ReportStatus.DRAFT,
            customerName = "Customer B",
            jobTitle = "Job B"
        )

        val result = agent.generateDraft(report, emptyList(), "Typed notes")

        assertTrue(result.isSuccess)
        assertEquals(report, result.getOrNull())
    }
}
