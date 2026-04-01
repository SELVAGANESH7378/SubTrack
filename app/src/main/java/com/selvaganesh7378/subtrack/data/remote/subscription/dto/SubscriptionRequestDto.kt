package com.selvaganesh7378.subtrack.data.remote.subscription.dto

data class SubscriptionRequestDto(
    val serviceName: String,
    val category: String,
    val cost: Double,
    val status: String,
    val nextRenewal: String,
    val remindMeIn: Int,
    val billingCycle: String,
    val paymentMethod: String,
    val brandColorHex: String,
    val currency: String,
    val notes: String
)
