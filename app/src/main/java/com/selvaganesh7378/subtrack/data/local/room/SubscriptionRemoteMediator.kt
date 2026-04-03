package com.selvaganesh7378.subtrack.data.local.room

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
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionDto
import kotlin.collections.emptyList
import kotlin.collections.isNotEmpty

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
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val apiStatus = when (status) {
                "Active" -> "active"
                "Cancelled" -> "canceled" // Your API expects 1 L
                else -> null
            }
            val apiCategory = if (category == "All") null else category
            val apiQuery = query.ifBlank { null }

            // Make your network request with filters
            val response = api.getSubscriptions(
                count = "all",
                page = page,
                limit = state.config.pageSize,
                status = apiStatus,
                category = apiCategory,
                serviceName = apiQuery
            )
            val responseBody = response.body()

            val subscriptionList = responseBody?.subscriptions ?: emptyList()
            val isEndOfList = subscriptionList.isEmpty()

            val apiSummary = responseBody?.summary

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

                // 1. Map the DTOs -> Domain -> Entities
                val entities = subscriptionList.map { it.toDomain().toEntity() }

                remoteKeysDao.insertAll(keys)
                // 2. Insert the mapped entities
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
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, SubscriptionEntity>): SubscriptionRemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { sub -> remoteKeysDao.remoteKeysId(sub.id) }
    }
}