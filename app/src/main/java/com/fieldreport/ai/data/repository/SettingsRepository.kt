package com.fieldreport.ai.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fieldreport.ai.data.model.AiAgentMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val PREF_AI_AGENT_MODE = stringPreferencesKey("ai_agent_mode")
    private val PREF_BUSINESS_NAME = stringPreferencesKey("business_name")
    private val PREF_CURRENCY = stringPreferencesKey("currency")
    private val PREF_TECHNICIAN_NAME = stringPreferencesKey("technician_name")

    val aiAgentModeFlow: Flow<AiAgentMode> = context.dataStore.data.map { preferences ->
        val modeStr = preferences[PREF_AI_AGENT_MODE] ?: AiAgentMode.CLOUD.name
        try {
            AiAgentMode.valueOf(modeStr)
        } catch (e: Exception) {
            AiAgentMode.CLOUD
        }
    }

    val businessNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PREF_BUSINESS_NAME] ?: ""
    }

    val currencyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PREF_CURRENCY] ?: "$"
    }

    val technicianNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PREF_TECHNICIAN_NAME] ?: ""
    }

    suspend fun setAiAgentMode(mode: AiAgentMode) {
        context.dataStore.edit { preferences ->
            preferences[PREF_AI_AGENT_MODE] = mode.name
        }
    }

    suspend fun setBusinessName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PREF_BUSINESS_NAME] = name
        }
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[PREF_CURRENCY] = currency
        }
    }

    suspend fun setTechnicianName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PREF_TECHNICIAN_NAME] = name
        }
    }
}
