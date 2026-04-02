package com.selvaganesh7378.subtrack.data.local.room.remotekeys

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<SubscriptionRemoteKeys>)

    @Query("SELECT * FROM subscription_remote_keys WHERE id = :id")
    suspend fun remoteKeysId(id: Int): SubscriptionRemoteKeys?

    @Query("DELETE FROM subscription_remote_keys")
    suspend fun clearRemoteKeys()
}