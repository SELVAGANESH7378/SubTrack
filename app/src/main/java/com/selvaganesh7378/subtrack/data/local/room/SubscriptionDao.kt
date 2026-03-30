package com.selvaganesh7378.subtrack.data.local.room


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    // Returns a Flow so your UI can automatically update whenever the database changes!
    @Query("SELECT * FROM subscriptions")
    fun getAllSubscriptionsFlow(): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriptions(subscriptions: List<SubscriptionEntity>)

    @Query("DELETE FROM subscriptions")
    suspend fun clearAllSubscriptions()

    // Replaces the old cached list with the fresh list from the API
    @Transaction
    suspend fun refreshSubscriptions(subscriptions: List<SubscriptionEntity>) {
        clearAllSubscriptions()
        insertSubscriptions(subscriptions)
    }
}