package com.selvaganesh7378.subtrack.data.remote.subscription.dto

data class SubscriptionSummaryDto(
    val limit: Int? = null,
    val totalActive: Int? = null,
    val monthlyCost: String? = null,
    val currency: String? = null
)
