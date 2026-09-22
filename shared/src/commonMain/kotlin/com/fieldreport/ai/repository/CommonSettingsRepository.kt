package com.fieldreport.ai.repository

import com.fieldreport.ai.model.SharedEntitlement
import com.fieldreport.ai.model.SharedTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CommonSettingsRepository {

    private val _entitlement = MutableStateFlow(
        SharedEntitlement(
            remainingPdfs = 3,
            isSubscribed = false,
            isLifetime = false,
            tier = SharedTier.FREE_TRIAL
        )
    )
    val entitlement: StateFlow<SharedEntitlement> = _entitlement.asStateFlow()

    fun updateEntitlement(
        remainingPdfs: Int,
        isSubscribed: Boolean,
        isLifetime: Boolean,
        tier: SharedTier
    ) {
        _entitlement.value = SharedEntitlement(
            remainingPdfs = remainingPdfs,
            isSubscribed = isSubscribed,
            isLifetime = isLifetime,
            tier = tier
        )
    }

    fun consumePdfCredit(): Boolean {
        val current = _entitlement.value
        if (current.isSubscribed || current.isLifetime) {
            return true
        }
        if (current.remainingPdfs > 0) {
            _entitlement.value = current.copy(remainingPdfs = current.remainingPdfs - 1)
            return true
        }
        return false
    }

    fun resetToFreeTrial() {
        _entitlement.value = SharedEntitlement(
            remainingPdfs = 3,
            isSubscribed = false,
            isLifetime = false,
            tier = SharedTier.FREE_TRIAL
        )
    }
}
