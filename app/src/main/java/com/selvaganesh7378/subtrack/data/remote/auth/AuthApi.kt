package com.selvaganesh7378.subtrack.data.remote.auth

import com.selvaganesh7378.subtrack.data.remote.auth.dto.logout.LogOutRequest
import com.selvaganesh7378.subtrack.data.remote.auth.dto.logout.LogOutResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.login.LoginRequest
import com.selvaganesh7378.subtrack.data.remote.auth.dto.login.LoginResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.refreshtoken.RefreshTokenRequest
import com.selvaganesh7378.subtrack.data.remote.auth.dto.refreshtoken.RefreshTokenResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.register.RegisterRequest
import com.selvaganesh7378.subtrack.data.remote.auth.dto.register.RegisterResponse
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

    @POST("auth/logout")
    suspend fun logout(
        @Body request: LogOutRequest
    ): Response<LogOutResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>
}

