package com.selvaganesh7378.subtrack.data.remote.subscription.dto

data class SubscriptionDto(
    val id: Int? = null,
    val serviceName: String? = null,
    val category: String? = null,
    val cost: Double? = null,
    val status: String? = null,
    val nextRenewal: String? = null,
    val remindMeIn: Int? = null,
    val billingCycle: String? = null,
    val paymentMethod: String? = null,
    val brandColorHex: String? = null,
    val currency: String? = null
)