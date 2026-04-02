package com.selvaganesh7378.subtrack.data.local.room.subscription


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM subscriptions ORDER BY id DESC")
    fun getPaginatedSubscriptions(): PagingSource<Int, SubscriptionEntity>

    @Query("SELECT * FROM subscriptions")
    fun getAllSubscriptionsFlow(): Flow<List<SubscriptionEntity>>

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteSubscriptionById(id: Int)

    @Query("SELECT * FROM subscriptions WHERE id = :id LIMIT 1")
    suspend fun getSubscriptionById(id: Int): SubscriptionEntity?

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