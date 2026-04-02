package com.selvaganesh7378.subtrack.domain.repository

import androidx.paging.PagingData
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionRequestDto
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription
import com.selvaganesh7378.subtrack.domain.model.subscription.SubscriptionsResult
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    // 1. Observe the local database continuously
//    fun getSubscriptionsStream(): Flow<List<Subscription>>

    // 2. Trigger a background network sync to update the database
    suspend fun syncSubscriptions(): LocalResult<Unit>

    suspend fun deleteSubscription(id: Int): LocalResult<Unit>

    suspend fun getSubscriptionById(id: Int): Subscription?

    suspend fun createSubscription(request: SubscriptionRequestDto): LocalResult<Unit>

    suspend fun updateSubscription(id: Int, request: SubscriptionRequestDto): LocalResult<Unit>

    fun getSubscriptionsStream(): Flow<PagingData<Subscription>>
}