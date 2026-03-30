package com.selvaganesh7378.subtrack.domain.model.subscription

data class Subscription(
    val id: Int,
    val serviceName: String,
    val category: String,
    val cost: Double,
    val status: String,
    val nextRenewal: String,
    val remindMeIn: Int,
    val billingCycle: String,
    val paymentMethod: String,
    val brandColorHex: String,
    val currency: String
)
