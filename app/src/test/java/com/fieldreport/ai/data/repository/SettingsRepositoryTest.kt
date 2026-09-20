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
    fun invalidAiAgentMode_fallsBackToCloud() = runTest {
        val PREF_AI_AGENT_MODE = stringPreferencesKey("ai_agent_mode")
        val prefs = preferencesOf(PREF_AI_AGENT_MODE to "INVALID_MODE")
        every { mockDataStore.data } returns flowOf(prefs)

        val repository = SettingsRepository(mockContext)

        assertEquals(AiAgentMode.CLOUD, repository.aiAgentModeFlow.first())
    }
}
