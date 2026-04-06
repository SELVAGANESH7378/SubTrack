package com.selvaganesh7378.subtrack.domain.repository

import androidx.paging.PagingData
import com.selvaganesh7378.subtrack.data.local.room.notification.NotificationEntity
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionSummaryEntity
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionRequestDto
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription
import com.selvaganesh7378.subtrack.domain.model.subscription.SubscriptionsResult
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {

    fun getSubscriptionsStream(query: String, status: String, category: String): Flow<PagingData<Subscription>>

//    suspend fun syncSubscriptions(): LocalResult<Unit>

    suspend fun deleteSubscription(id: Int): LocalResult<Unit>

    suspend fun getSubscriptionById(id: Int): Subscription?

    suspend fun createSubscription(request: SubscriptionRequestDto): LocalResult<Unit>

    suspend fun updateSubscription(id: Int, request: SubscriptionRequestDto): LocalResult<Unit>

    fun getSubscriptionSummary(): Flow<SubscriptionSummaryEntity?>

    suspend fun getUpcomingNotifications(): LocalResult<List<Subscription>>

    fun getNotificationsFlow(): Flow<List<NotificationEntity>>

    suspend fun syncNotifications(): LocalResult<Unit>

    suspend fun markNotificationAsRead(id: Int)

    suspend fun markAllNotificationsAsRead()

}