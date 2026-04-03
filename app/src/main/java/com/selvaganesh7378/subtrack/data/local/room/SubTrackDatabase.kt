package com.selvaganesh7378.subtrack.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.selvaganesh7378.subtrack.data.local.room.remotekeys.RemoteKeysDao
import com.selvaganesh7378.subtrack.data.local.room.remotekeys.SubscriptionRemoteKeysEntity
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionDao
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionEntity
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionSummaryEntity

@Database(
    entities = [
        SubscriptionEntity::class,
        SubscriptionRemoteKeysEntity::class,
        SubscriptionSummaryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class SubTrackDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}