package com.selvaganesh7378.subtrack.domain.repository


import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.auth.LogOutResult
import com.selvaganesh7378.subtrack.domain.model.auth.LoginResult
import com.selvaganesh7378.subtrack.domain.model.auth.RegisterResult

interface AuthRepository {
    suspend fun signIn(email: String, password: String): LocalResult<LoginResult>

    suspend fun signUp(name: String, email: String, password: String, currentTimeZone: String): LocalResult<RegisterResult>

    suspend fun logout(refreshToken: String): LocalResult<LogOutResult>
}