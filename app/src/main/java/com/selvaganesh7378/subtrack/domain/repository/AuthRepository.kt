package com.selvaganesh7378.subtrack.domain.repository

import com.selvaganesh7378.subtrack.data.remote.auth.dto.LogOutResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.LoginResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.RegisterResponse
import com.selvaganesh7378.subtrack.domain.LocalResult

interface AuthRepository {
    suspend fun signIn(email: String, password: String): LocalResult<LoginResponse>

    suspend fun signUp(name: String, email: String, password: String, currentTimeZone: String): LocalResult<RegisterResponse>

    suspend fun logout(refreshToken: String): LocalResult<LogOutResponse>
}