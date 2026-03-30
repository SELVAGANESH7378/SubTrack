package com.selvaganesh7378.subtrack.data.repository

import com.selvaganesh7378.subtrack.data.local.room.SubscriptionDao
import com.selvaganesh7378.subtrack.data.mapper.toDomain
import com.selvaganesh7378.subtrack.data.mapper.toEntity
import com.selvaganesh7378.subtrack.data.remote.subscription.SubscriptionApi
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription
import com.selvaganesh7378.subtrack.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionApi: SubscriptionApi,
    private val subscriptionDao: SubscriptionDao
) : SubscriptionRepository {

    // 1. UI observes this. It automatically updates whenever Room changes.
    override fun getSubscriptionsStream(): Flow<List<Subscription>> {
        return subscriptionDao.getAllSubscriptionsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // 2. ViewModel calls this to fetch from API and save to Room.
    override suspend fun syncSubscriptions(): LocalResult<Unit> {
        return try {
            val response = subscriptionApi.getAllSubscriptions()

            if (response.isSuccessful && response.body() != null) {
                // Get the DTOs from the network
                val networkSubscriptions = response.body()!!.subscriptions ?: emptyList()

                // Map DTO -> Domain -> Room Entity
                val entitiesToSave = networkSubscriptions.map { it.toDomain().toEntity() }

                // Save to Room. This automatically triggers the Flow above to emit new data!
                subscriptionDao.refreshSubscriptions(entitiesToSave)

                LocalResult.Success(Unit)
            } else {
                LocalResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            LocalResult.Error("Network error. Working offline.")
        } catch (e: HttpException) {
            LocalResult.Error("Server error occurred (${e.code()}).")
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}