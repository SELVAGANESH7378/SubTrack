package com.selvaganesh7378.subtrack.data.local.room.remotekeys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_remote_keys")
data class SubscriptionRemoteKeys(
    @PrimaryKey val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)