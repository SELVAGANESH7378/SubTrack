package com.selvaganesh7378.subtrack.data.local.room

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.selvaganesh7378.subtrack.data.local.room.remotekeys.SubscriptionRemoteKeys
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionEntity
import com.selvaganesh7378.subtrack.data.remote.subscription.SubscriptionApi
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalPagingApi::class)
class SubscriptionRemoteMediator(
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

            // Make your network request here
            val response = api.getSubscriptions(page = page, limit = state.config.pageSize)
            val isEndOfList = response.isEmpty()

            db.withTransaction {
                // Clear out old data if we are refreshing
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    subscriptionDao.clearAllSubscriptions()
                }

                // Calculate the next and previous keys
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1

                val keys = response.map {
                    SubscriptionRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                // Save everything to Room
                remoteKeysDao.insertAll(keys)
                subscriptionDao.insertSubscriptions(response)
            }

            MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, SubscriptionEntity>): SubscriptionRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { sub -> remoteKeysDao.remoteKeysId(sub.id) }
    }
}