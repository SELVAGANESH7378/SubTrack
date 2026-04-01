package com.selvaganesh7378.subtrack.data.remote.subscription

import com.selvaganesh7378.subtrack.data.remote.subscription.dto.DeleteSubscriptionResponseDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SingleSubscriptionResponseDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionRequestDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionsResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SubscriptionApi {

    @GET("subscription/all")
    suspend fun getAllSubscriptions(): Response<SubscriptionsResponseDto>

    @DELETE("subscription/delete/{id}")
    suspend fun deleteSubscription(@Path("id") id: Int): Response<DeleteSubscriptionResponseDto>

    @POST("subscription/create")
    suspend fun createSubscription(@Body request: SubscriptionRequestDto): Response<SingleSubscriptionResponseDto>

    @PUT("subscription/update/{id}")
    suspend fun updateSubscription(@Path("id") id: Int, @Body request: SubscriptionRequestDto): Response<SingleSubscriptionResponseDto>
}