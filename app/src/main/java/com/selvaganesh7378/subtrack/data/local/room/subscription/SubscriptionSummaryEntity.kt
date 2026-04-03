package com.selvaganesh7378.subtrack.data.local.room.subscription

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_summary")
data class SubscriptionSummaryEntity(
    @PrimaryKey val id: Int = 1,
    val totalActive: Int,
    val monthlyCost: String,
    val currency: String
)