package com.selvaganesh7378.subtrack.data.local.room

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.selvaganesh7378.subtrack.data.local.room.remotekeys.SubscriptionRemoteKeysEntity
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionEntity
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionSummaryEntity
import com.selvaganesh7378.subtrack.data.mapper.toDomain
import com.selvaganesh7378.subtrack.data.mapper.toEntity
import com.selvaganesh7378.subtrack.data.remote.subscription.SubscriptionApi
import kotlin.collections.emptyList

@OptIn(ExperimentalPagingApi::class)
class SubscriptionRemoteMediator(
    private val query: String,
    private val status: String,
    private val category: String,
    private val api: SubscriptionApi,
    private val db: SubTrackDatabase
) : RemoteMediator<Int, SubscriptionEntity>() {

    private val subscriptionDao = db.subscriptionDao()
    private val remoteKeysDao = db.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SubscriptionEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    if (nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    nextKey
                }
            }

            Log.d("mediator", "Page: $page")

            val apiStatus = when (status) {
                "Active" -> "active"
                "Cancelled" -> "canceled"
                else -> null
            }
            val apiCategory = if (category == "All") null else category
            val apiQuery = query.ifBlank { null }

            val response = api.getSubscriptions(
                page = page,
                limit = state.config.pageSize,
                status = apiStatus,
                category = apiCategory,
                serviceName = apiQuery
            )

            if (!response.isSuccessful) {
                throw retrofit2.HttpException(response)
            }

            val responseBody = response.body()

            val subscriptionList = responseBody?.subscriptions ?: emptyList()
            val paginationMeta = responseBody?.pagination
            
            // Check pagination info from API response - prefer hasNextPage if available
            val hasNextPage = paginationMeta?.hasNextPage == true
            val isEndOfList = !hasNextPage || subscriptionList.isEmpty()
            val apiSummary = responseBody?.summary

            Log.d("mediator", "hasNextPage = ${paginationMeta?.hasNextPage}")
            Log.d("mediator", "hasPrevPage = ${paginationMeta?.hasPrevPage}")
            Log.d("mediator", "isEndOfList = $isEndOfList")
            Log.d("mediator", "subscriptionList size = ${subscriptionList.size}")


            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    subscriptionDao.clearAllSubscriptions()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1

                val keys = subscriptionList.map {
                    SubscriptionRemoteKeysEntity(id = it.id ?: 0, prevKey = prevKey, nextKey = nextKey)
                }

                val entities = subscriptionList.map { it.toDomain().toEntity() }

                remoteKeysDao.insertAll(keys)
                subscriptionDao.insertSubscriptions(entities)

                if (apiSummary != null) {
                    subscriptionDao.insertSummary(
                        SubscriptionSummaryEntity(
                            id = 1,
                            totalActive = apiSummary.totalActive ?: 0,
                            monthlyCost = apiSummary.monthlyCost ?: "0.0",
                            currency = apiSummary.currency ?: "₹"
                        )
                    )
                }
            }

            MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (e: Exception) {
            Log.e("mediator", "Error loading subscriptions", e)
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, SubscriptionEntity>): SubscriptionRemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { sub -> remoteKeysDao.remoteKeysId(sub.id) }
    }
}