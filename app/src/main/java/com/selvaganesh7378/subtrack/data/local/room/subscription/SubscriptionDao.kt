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

    // 1. Paginated & Filtered Query
    @Query("""
        SELECT * FROM subscriptions 
        WHERE (:query = '' OR serviceName LIKE '%' || :query || '%')
        AND (:status = 'All' OR status COLLATE NOCASE = :status)
        AND (:category = 'All' OR category COLLATE NOCASE = :category)
        ORDER BY id ASC
    """)
    fun getPaginatedSubscriptions(query: String, status: String, category: String): PagingSource<Int, SubscriptionEntity>

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteSubscriptionById(id: Int)

    @Query("SELECT * FROM subscriptions WHERE id = :id LIMIT 1")
    suspend fun getSubscriptionById(id: Int): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriptions(subscriptions: List<SubscriptionEntity>)

    @Query("DELETE FROM subscriptions")
    suspend fun clearAllSubscriptions()

    @Transaction
    suspend fun refreshSubscriptions(subscriptions: List<SubscriptionEntity>) {
        clearAllSubscriptions()
        insertSubscriptions(subscriptions)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SubscriptionSummaryEntity)

    @Query("SELECT * FROM subscription_summary WHERE id = 1")
    fun getSummaryFlow(): Flow<SubscriptionSummaryEntity?>
}