package com.fieldreport.ai.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fieldreport.ai.data.model.AiAgentMode
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsRepositoryTest {

    private val mockContext = mockk<Context>(relaxed = true)
    private val mockDataStore = mockk<DataStore<Preferences>>(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic("com.fieldreport.ai.data.repository.SettingsRepositoryKt")
        every { mockContext.dataStore } returns mockDataStore
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun defaults_returnsExpectedValues() = runTest {
        every { mockDataStore.data } returns flowOf(emptyPreferences())

        val repository = SettingsRepository(mockContext)

        assertEquals(AiAgentMode.CLOUD, repository.aiAgentModeFlow.first())
        assertEquals("", repository.businessNameFlow.first())
        assertEquals("$", repository.currencyFlow.first())
        assertEquals("", repository.technicianNameFlow.first())
    }

    @Test
    fun customPreferences_returnsValues() = runTest {
        val PREF_AI_AGENT_MODE = stringPreferencesKey("ai_agent_mode")
        val PREF_BUSINESS_NAME = stringPreferencesKey("business_name")
        val PREF_CURRENCY = stringPreferencesKey("currency")
        val PREF_TECHNICIAN_NAME = stringPreferencesKey("technician_name")

        val prefs = preferencesOf(
            PREF_AI_AGENT_MODE to "ON_DEVICE",
            PREF_BUSINESS_NAME to "Apex Plumbing",
            PREF_CURRENCY to "EUR",
            PREF_TECHNICIAN_NAME to "Alex Smith"
        )
        every { mockDataStore.data } returns flowOf(prefs)

        val repository = SettingsRepository(mockContext)

        assertEquals(AiAgentMode.ON_DEVICE, repository.aiAgentModeFlow.first())
        assertEquals("Apex Plumbing", repository.businessNameFlow.first())
        assertEquals("EUR", repository.currencyFlow.first())
        assertEquals("Alex Smith", repository.technicianNameFlow.first())
    }

    @Test
    fun themeMode_returnsExpectedValuesAndFallback() = runTest {
        val PREF_THEME_MODE = stringPreferencesKey("theme_mode")
        val darkPrefs = preferencesOf(PREF_THEME_MODE to "DARK")
        every { mockDataStore.data } returns flowOf(darkPrefs)

        val repo1 = SettingsRepository(mockContext)
        assertEquals(com.fieldreport.ai.data.model.ThemeMode.DARK, repo1.themeModeFlow.first())

        val invalidPrefs = preferencesOf(PREF_THEME_MODE to "INVALID")
        every { mockDataStore.data } returns flowOf(invalidPrefs)
        val repo2 = SettingsRepository(mockContext)
        assertEquals(com.fieldreport.ai.data.model.ThemeMode.SYSTEM, repo2.themeModeFlow.first())
    }

    @Test
    fun brandingAndEntitlementFlows_returnExpectedValues() = runTest {
        val PREF_COMPANY_LOGO_URI = stringPreferencesKey("company_logo_uri")
        val PREF_SIGNATURE_URI = stringPreferencesKey("signature_uri")
        val PREF_FREE_PDFS_REMAINING = androidx.datastore.preferences.core.intPreferencesKey("free_pdfs_remaining")
        val PREF_IS_SUBSCRIBED = androidx.datastore.preferences.core.booleanPreferencesKey("is_subscribed")
        val PREF_IS_LIFETIME = androidx.datastore.preferences.core.booleanPreferencesKey("is_lifetime")
        val PREF_SUBSCRIPTION_TIER = stringPreferencesKey("subscription_tier")

        val prefs = preferencesOf(
            PREF_COMPANY_LOGO_URI to "content://logo.png",
            PREF_SIGNATURE_URI to "content://sig.png",
            PREF_FREE_PDFS_REMAINING to 5,
            PREF_IS_SUBSCRIBED to true,
            PREF_IS_LIFETIME to false,
            PREF_SUBSCRIPTION_TIER to "PRO_MONTHLY"
        )
        every { mockDataStore.data } returns flowOf(prefs)

        val repository = SettingsRepository(mockContext)

        assertEquals("content://logo.png", repository.companyLogoUriFlow.first())
        assertEquals("content://sig.png", repository.signatureUriFlow.first())
        assertEquals(5, repository.freePdfsRemainingFlow.first())
        assertEquals(true, repository.isSubscribedFlow.first())
        assertEquals(false, repository.isLifetimeFlow.first())
        assertEquals("PRO_MONTHLY", repository.subscriptionTierFlow.first())
    }

    @Test
    fun setters_invokeDataStoreEdit() = runTest {
        coEvery { mockDataStore.updateData(any()) } returns emptyPreferences()

        val repository = SettingsRepository(mockContext)

        repository.setAiAgentMode(AiAgentMode.ON_DEVICE)
        repository.setThemeMode(com.fieldreport.ai.data.model.ThemeMode.DARK)
        repository.setBusinessName("Test Business")
        repository.setCurrency("CAD")
        repository.setTechnicianName("Tech Name")
        repository.setCompanyLogoUri("content://logo")
        repository.setCompanyLogoUri(null)
        repository.setSignatureUri("content://sig")
        repository.setSignatureUri(null)
        repository.setFreePdfsRemaining(10)
        repository.setSubscribed(true)
        repository.setLifetime(true)
        repository.setSubscriptionTier("PRO_LIFETIME")
        repository.updateEntitlement(999, true, true, "PRO_LIFETIME")

        coVerify(atLeast = 1) { mockDataStore.updateData(any()) }
    }
}
