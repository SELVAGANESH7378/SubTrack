package com.selvaganesh7378.subtrack.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.selvaganesh7378.subtrack.data.local.room.SubTrackDatabase
import com.selvaganesh7378.subtrack.data.local.room.SubscriptionRemoteMediator
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionDao
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionSummaryEntity
import com.selvaganesh7378.subtrack.data.mapper.toDomain
import com.selvaganesh7378.subtrack.data.mapper.toEntity
import com.selvaganesh7378.subtrack.data.remote.subscription.SubscriptionApi
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionRequestDto
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription
import com.selvaganesh7378.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionApi: SubscriptionApi,
    private val subscriptionDao: SubscriptionDao,
    private val db: SubTrackDatabase
) : SubscriptionRepository {


    @OptIn(ExperimentalPagingApi::class)
    override fun getSubscriptionsStream(query: String, status: String, category: String): Flow<PagingData<Subscription>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 3),
            remoteMediator = SubscriptionRemoteMediator(query, status, category, subscriptionApi, db),
            pagingSourceFactory = {
                // Also pass it to the DAO!
                subscriptionDao.getPaginatedSubscriptions(query, status, category)
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }


    // 3. Delete logic: API first, Room second.
    override suspend fun deleteSubscription(id: Int): LocalResult<Unit> {
        return try {
            // 1. Delete Online First
            val response = subscriptionApi.deleteSubscription(id)

            if (response.isSuccessful) {
                // 2. If successful, Delete in Room
                subscriptionDao.deleteSubscriptionById(id)

                LocalResult.Success(Unit)
            } else {
                LocalResult.Error(mapErrorCode(response.code(), response.message()))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            LocalResult.Error("Network error. Cannot delete while offline.")
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "Unexpected error")
        }
    }

    override suspend fun getSubscriptionById(id: Int): Subscription? {
        return subscriptionDao.getSubscriptionById(id)?.toDomain()
    }

    override suspend fun createSubscription(request: SubscriptionRequestDto): LocalResult<Unit> {
        return try {
            val response = subscriptionApi.createSubscription(request)

            if (response.isSuccessful && response.body()?.subscription != null) {
                // Save the new subscription to Room to instantly update the UI
                val newSub = response.body()!!.subscription!!
                Log.e("subsrepository", "color: ${newSub.brandColorHex} with ID ${newSub.id}")
                val entitySub = newSub.toDomain().toEntity()
                subscriptionDao.insertSubscriptions(listOf(entitySub))
                LocalResult.Success(Unit)
            } else {
                LocalResult.Error(mapErrorCode(response.code(), response.message()))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            LocalResult.Error("Network error. Cannot create while offline.")
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "Unexpected error")
        }
    }

    override fun getSubscriptionSummary(): Flow<SubscriptionSummaryEntity?> {
        return subscriptionDao.getSummaryFlow()
    }

    override suspend fun updateSubscription(id: Int, request: SubscriptionRequestDto): LocalResult<Unit> {
        return try {
            val response = subscriptionApi.updateSubscription(id, request)

            if (response.isSuccessful && response.body()?.subscription != null) {
                // Overwrite the existing subscription in Room
                val updatedSub = response.body()!!.subscription!!.toDomain().toEntity()
                subscriptionDao.insertSubscriptions(listOf(updatedSub))
                LocalResult.Success(Unit)
            } else {
                LocalResult.Error(mapErrorCode(response.code(), response.message()))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            LocalResult.Error("Network error. Cannot update while offline.")
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "Unexpected error")
        }
    }

    private fun mapErrorCode(code: Int, message: String? = null): String {
        return when (code) {
            400 -> "All fields are required"
            401 -> "Unauthorized"
            404 -> "Subscription not found"
            500 -> "Internal server error"
            else -> "Error $code: ${message ?: "Unknown error"}"
        }
    }
}