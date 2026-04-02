package com.selvaganesh7378.subtrack.data.local.room.subscription

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: Int,
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