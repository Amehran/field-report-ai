package com.fieldreport.ai.model

import kotlinx.serialization.Serializable

enum class SharedTier {
    FREE_TRIAL,
    PRO_SUBSCRIBED,
    LIFETIME_PASS
}

@Serializable
data class SharedEntitlement(
    val remainingPdfs: Int = 3,
    val isSubscribed: Boolean = false,
    val isLifetime: Boolean = false,
    val tier: SharedTier = SharedTier.FREE_TRIAL
)
