package com.selvaganesh7378.subtrack.data.remote.subscription

import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionsResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface SubscriptionApi {

    @GET("subscription/all")
    suspend fun getAllSubscriptions(): Response<SubscriptionsResponseDto>
}