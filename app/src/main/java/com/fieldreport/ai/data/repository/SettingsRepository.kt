package com.fieldreport.ai.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fieldreport.ai.data.model.AiAgentMode
import com.fieldreport.ai.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val PREF_AI_AGENT_MODE = stringPreferencesKey("ai_agent_mode")
    private val PREF_THEME_MODE = stringPreferencesKey("theme_mode")
    private val PREF_BUSINESS_NAME = stringPreferencesKey("business_name")
    private val PREF_CURRENCY = stringPreferencesKey("currency")
    private val PREF_TECHNICIAN_NAME = stringPreferencesKey("technician_name")
    private val PREF_FREE_PDFS_REMAINING = intPreferencesKey("free_pdfs_remaining")
    private val PREF_IS_SUBSCRIBED = booleanPreferencesKey("is_subscribed")
    private val PREF_IS_LIFETIME = booleanPreferencesKey("is_lifetime")
    private val PREF_SUBSCRIPTION_TIER = stringPreferencesKey("subscription_tier")
    private val PREF_COMPANY_LOGO_URI = stringPreferencesKey("company_logo_uri")
    private val PREF_SIGNATURE_URI = stringPreferencesKey("signature_uri")

    val aiAgentModeFlow: Flow<AiAgentMode> = context.dataStore.data.map { preferences ->
        val modeStr = preferences[PREF_AI_AGENT_MODE] ?: AiAgentMode.CLOUD.name
        try {
            AiAgentMode.valueOf(modeStr)
        } catch (e: Exception) {
            AiAgentMode.CLOUD
        }
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val modeStr = preferences[PREF_THEME_MODE] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(modeStr)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
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

    val freePdfsRemainingFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PREF_FREE_PDFS_REMAINING] ?: 3
    }

    val isSubscribedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PREF_IS_SUBSCRIBED] ?: false
    }

    val isLifetimeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PREF_IS_LIFETIME] ?: false
    }

    val subscriptionTierFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PREF_SUBSCRIPTION_TIER] ?: "FREE_TRIAL"
    }

    val companyLogoUriFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PREF_COMPANY_LOGO_URI]
    }

    val signatureUriFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PREF_SIGNATURE_URI]
    }

    suspend fun setAiAgentMode(mode: AiAgentMode) {
        context.dataStore.edit { preferences ->
            preferences[PREF_AI_AGENT_MODE] = mode.name
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PREF_THEME_MODE] = mode.name
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

    suspend fun setCompanyLogoUri(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri != null) {
                preferences[PREF_COMPANY_LOGO_URI] = uri
            } else {
                preferences.remove(PREF_COMPANY_LOGO_URI)
            }
        }
    }

    suspend fun setSignatureUri(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri != null) {
                preferences[PREF_SIGNATURE_URI] = uri
            } else {
                preferences.remove(PREF_SIGNATURE_URI)
            }
        }
    }

    suspend fun setFreePdfsRemaining(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[PREF_FREE_PDFS_REMAINING] = count
        }
    }

    suspend fun setSubscribed(subscribed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PREF_IS_SUBSCRIBED] = subscribed
        }
    }

    suspend fun setLifetime(lifetime: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PREF_IS_LIFETIME] = lifetime
        }
    }

    suspend fun setSubscriptionTier(tier: String) {
        context.dataStore.edit { preferences ->
            preferences[PREF_SUBSCRIPTION_TIER] = tier
        }
    }

    private val commonSettingsRepo = com.fieldreport.ai.repository.CommonSettingsRepository()

    val commonEntitlement = commonSettingsRepo.entitlement

    suspend fun updateEntitlement(remainingPdfs: Int, isSubscribed: Boolean, isLifetime: Boolean, tier: String) {
        val sharedTier = when {
            isLifetime -> com.fieldreport.ai.model.SharedTier.LIFETIME_PASS
            isSubscribed -> com.fieldreport.ai.model.SharedTier.PRO_SUBSCRIBED
            else -> com.fieldreport.ai.model.SharedTier.FREE_TRIAL
        }
        commonSettingsRepo.updateEntitlement(remainingPdfs, isSubscribed, isLifetime, sharedTier)
        
        context.dataStore.edit { preferences ->
            preferences[PREF_FREE_PDFS_REMAINING] = remainingPdfs
            preferences[PREF_IS_SUBSCRIBED] = isSubscribed
            preferences[PREF_IS_LIFETIME] = isLifetime
            preferences[PREF_SUBSCRIPTION_TIER] = tier
        }
    }
}
