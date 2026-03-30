package com.selvaganesh7378.subtrack.domain.repository

import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription
import com.selvaganesh7378.subtrack.domain.model.subscription.SubscriptionsResult
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    // 1. Observe the local database continuously
    fun getSubscriptionsStream(): Flow<List<Subscription>>

    // 2. Trigger a background network sync to update the database
    suspend fun syncSubscriptions(): LocalResult<Unit>
}