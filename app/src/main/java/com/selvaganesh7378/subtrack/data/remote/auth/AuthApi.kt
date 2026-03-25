package com.selvaganesh7378.subtrack.data.remote.auth

import com.selvaganesh7378.subtrack.data.remote.auth.dto.LoginRequest
import com.selvaganesh7378.subtrack.data.remote.auth.dto.LoginResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.RegisterRequest
import com.selvaganesh7378.subtrack.data.remote.auth.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("auth/refresh") // Change this to your actual refresh endpoint
    suspend fun refreshToken(
        @Body refreshToken: Map<String, String> // e.g., {"refreshToken": "your_token"}
    ): retrofit2.Response<LoginResponse> // Assuming the server returns the same token format
}

