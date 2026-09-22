package com.fieldreport.ai.repository

import com.fieldreport.ai.model.SharedReport
import com.fieldreport.ai.model.SharedTier
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RepositoryTest {

    @Test
    fun testCommonSettingsRepositoryEntitlementConsumption() {
        val repo = CommonSettingsRepository()
        assertEquals(3, repo.entitlement.value.remainingPdfs)
        assertFalse(repo.entitlement.value.isSubscribed)

        // Consume 1 credit
        val result1 = repo.consumePdfCredit()
        assertTrue(result1)
        assertEquals(2, repo.entitlement.value.remainingPdfs)

        // Consume remaining credits
        repo.consumePdfCredit()
        repo.consumePdfCredit()
        assertEquals(0, repo.entitlement.value.remainingPdfs)

        // Out of credits
        val resultOut = repo.consumePdfCredit()
        assertFalse(resultOut)

        // Upgrade to Pro
        repo.updateEntitlement(999, isSubscribed = true, isLifetime = false, tier = SharedTier.PRO_SUBSCRIBED)
        assertTrue(repo.entitlement.value.isSubscribed)
        assertTrue(repo.consumePdfCredit())
    }

    @Test
    fun testCommonReportRepositoryAddAndDelete() {
        val repo = CommonReportRepository()
        assertEquals(0, repo.reports.value.size)

        val report = SharedReport(id = "rep_1", title = "Test Safety Audit")
        repo.addReport(report)
        assertEquals(1, repo.reports.value.size)
        assertEquals("Test Safety Audit", repo.reports.value.first().title)

        repo.deleteReport("rep_1")
        assertEquals(0, repo.reports.value.size)
    }
}
