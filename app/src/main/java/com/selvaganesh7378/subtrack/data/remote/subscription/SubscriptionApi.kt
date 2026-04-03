package com.selvaganesh7378.subtrack.data.remote.subscription

import com.selvaganesh7378.subtrack.data.remote.subscription.dto.delete.DeleteSubscriptionResponseDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SingleSubscriptionResponseDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionRequestDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionsResponseDto
import com.selvaganesh7378.subtrack.domain.model.subscription.SubscriptionsResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SubscriptionApi {

    @GET("subscription/{count}")
    suspend fun getSubscriptions(
        @Path("count") count: String = "all",
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: String? = null,
        @Query("category") category: String? = null,
        @Query("serviceName") serviceName: String? = null
    ): Response<SubscriptionsResponseDto>

    @DELETE("subscription/delete/{id}")
    suspend fun deleteSubscription(@Path("id") id: Int): Response<DeleteSubscriptionResponseDto>

    @POST("subscription/create")
    suspend fun createSubscription(@Body request: SubscriptionRequestDto): Response<SingleSubscriptionResponseDto>

    @PUT("subscription/update/{id}")
    suspend fun updateSubscription(@Path("id") id: Int, @Body request: SubscriptionRequestDto): Response<SingleSubscriptionResponseDto>
}