package com.selvaganesh7378.subtrack.data.repository

import com.selvaganesh7378.subtrack.data.local.TokenManager
import com.selvaganesh7378.subtrack.data.local.UserDataStore
import com.selvaganesh7378.subtrack.data.remote.auth.AuthApi
import com.selvaganesh7378.subtrack.data.remote.auth.dto.LogOutRequest
import com.selvaganesh7378.subtrack.data.remote.auth.dto.LogOutResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.LoginRequest
import com.selvaganesh7378.subtrack.data.remote.auth.dto.LoginResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.RegisterRequest
import com.selvaganesh7378.subtrack.data.remote.auth.dto.RegisterResponse
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.repository.AuthRepository
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val userDataStore: UserDataStore
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): LocalResult<LoginResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = authApi.login(request)

            if (response.isSuccessful && response.body() != null) {
                val loginData = response.body()!!
                tokenManager.saveTokens(
                    accessToken = loginData.accessToken,
                    refreshToken = loginData.refreshToken
                )
                userDataStore.saveUser(loginData.user)
                LocalResult.Success(loginData)
            } else {
                val errorMessage = try {
                    val errorJsonString = response.errorBody()?.string()

                    if (!errorJsonString.isNullOrEmpty()) {
                        val jsonObject = JSONObject(errorJsonString)
                        jsonObject.getString("message")
                    } else {
                        "Login failed: ${response.message()}"
                    }
                } catch (e: Exception) {
                    "server down try again later"
                }

                LocalResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): LocalResult<RegisterResponse> {
        return try {
            val request = RegisterRequest(name, email, password)
            val response = authApi.register(request)

            if (response.isSuccessful && response.body() != null) {
                LocalResult.Success(response.body()!!)
            } else {
                LocalResult.Error("Registration failed: ${response.message()}")
            }
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun logout(refreshToken: String): LocalResult<LogOutResponse> {
        return try {
            val request = LogOutRequest(refreshToken)
            val response = authApi.logout(request)
            if (response.isSuccessful && response.body() != null) {
                userDataStore.clearUser()
                LocalResult.Success(response.body()!!)
            } else {
                LocalResult.Error("Logout failed: ${response.message()}")
            }
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "An unexpected error occurred")

        }
    }
}