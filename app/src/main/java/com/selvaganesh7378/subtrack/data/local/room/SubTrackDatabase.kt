package com.selvaganesh7378.subtrack.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.selvaganesh7378.subtrack.data.local.room.remotekeys.RemoteKeysDao
import com.selvaganesh7378.subtrack.data.local.room.remotekeys.SubscriptionRemoteKeys
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionDao
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionEntity

@Database(
    entities = [
        SubscriptionEntity::class,
        SubscriptionRemoteKeys::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SubTrackDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}