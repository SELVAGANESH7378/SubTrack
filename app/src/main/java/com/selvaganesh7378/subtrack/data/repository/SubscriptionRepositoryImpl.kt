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

    // 1. UI observes this. It automatically updates whenever Room changes.
//    override fun getSubscriptionsStream(): Flow<List<Subscription>> {
//        return subscriptionDao.getAllSubscriptionsFlow().map { entities ->
//            entities.map { it.toDomain() }
//        }
//    }
    @OptIn(ExperimentalPagingApi::class)
    override fun getSubscriptionsStream(): Flow<PagingData<Subscription>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5
            ),
            // The Mediator handles fetching from API and saving to Room
            remoteMediator = SubscriptionRemoteMediator(
                api = subscriptionApi,
                db = db
            ),
            // Room acts as the single source of truth
            pagingSourceFactory = {
                subscriptionDao.getPaginatedSubscriptions()
            }
        ).flow.map { pagingData ->
            // Map the Room entities to Domain models before sending to ViewModel
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun syncSubscriptions(): LocalResult<Unit> {
        return try {
            val response = subscriptionApi.getAllSubscriptions()

            if (response.isSuccessful && response.body() != null) {

                val networkSubscriptions = response.body()!!.subscriptions ?: emptyList()
                val entitiesToSave = networkSubscriptions.map {
                    Log.e("subsrepository", "color: ${it.brandColorHex} with ID ${it.id}")
                    it.toDomain().toEntity()
                }

                subscriptionDao.refreshSubscriptions(entitiesToSave)

                LocalResult.Success(Unit)

            } else {
                LocalResult.Error(mapErrorCode(response.code(), response.message()))
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            LocalResult.Error("Network error. Working offline.")
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "Unexpected error")
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