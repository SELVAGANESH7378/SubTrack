package com.selvaganesh7378.subtrack.data.repository

import android.util.Log
import com.selvaganesh7378.subtrack.data.local.TokenManager
import com.selvaganesh7378.subtrack.data.local.datastore.UserDataStore
import com.selvaganesh7378.subtrack.data.mapper.toDomain
import com.selvaganesh7378.subtrack.data.remote.auth.AuthApi
import com.selvaganesh7378.subtrack.data.remote.auth.dto.logout.LogOutRequest
import com.selvaganesh7378.subtrack.data.remote.auth.dto.login.LoginRequestDto
import com.selvaganesh7378.subtrack.data.remote.auth.dto.register.RegisterRequest
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.auth.LogOutResult
import com.selvaganesh7378.subtrack.domain.model.auth.LoginResult
import com.selvaganesh7378.subtrack.domain.model.auth.RegisterResult
import com.selvaganesh7378.subtrack.domain.repository.AuthRepository
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val userDataStore: UserDataStore
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): LocalResult<LoginResult> {
        return try {
            val request = LoginRequestDto(email, password)
            val response = authApi.login(request)

            if (response.isSuccessful) {
                val loginData = response.body()
                    ?: return LocalResult.Error("Login failed: Empty response body")
                Log.e("authrepo", "loginData: accessToken=${loginData.accessToken}, refreshToken=${loginData.refreshToken}, userId=${loginData.user.uid}, userName=${loginData.user.name}, userEmail=${loginData.user.email}, userTimezone=${loginData.user.timezone}" +
                        ", userCreatedAt=${loginData.user.createdAt}, userImg=${loginData.user.img}")

                tokenManager.saveTokens(
                    accessToken = loginData.accessToken,
                    refreshToken = loginData.refreshToken
                )
                userDataStore.saveUserProfile(loginData.user.toDomain())
                LocalResult.Success(loginData.toDomain())

            } else {
                val errorMessage = parseErrorMessage(response)
                Log.e("authrepo", "errorMessage: $errorMessage")

                LocalResult.Error(errorMessage)
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            LocalResult.Error("Network error. Please check your connection.")
        } catch (e: HttpException) {
            val message = when (e.code()) {
                401 -> "Invalid email or password"
                403 -> "Access denied. Please contact support."
                404 -> "Account not found. Please check your email."
                408 -> "Request timed out. Try again."
                500, 502, 503 -> "Server is down. Please try again later."
                else -> "Something went wrong (${e.code()})"
            }
            LocalResult.Error(message)
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }



    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        currentTimeZone: String
    ): LocalResult<RegisterResult> {
        return try {
            val request = RegisterRequest(name, email, password, currentTimeZone)
            val response = authApi.register(request)

            if (response.isSuccessful && response.body() != null) {
                userDataStore.saveTimeZone(currentTimeZone)
                LocalResult.Success(response.body()!!.toDomain())
            } else {
                LocalResult.Error("Registration failed: ${response.message()}")
            }
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun logout(refreshToken: String): LocalResult<LogOutResult> {
        return try {
            val request = LogOutRequest(refreshToken)
            val response = authApi.logout(request)
            if (response.isSuccessful && response.body() != null) {
                userDataStore.clearUser()
                tokenManager.clearTokens()
                LocalResult.Success(response.body()!!.toDomain())
            } else {
                LocalResult.Error("Logout failed: ${response.message()}")
            }
        } catch (e: Exception) {
            LocalResult.Error(e.localizedMessage ?: "An unexpected error occurred")

        }
    }

    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorJsonString = response.errorBody()?.string() // Read only once
            if (!errorJsonString.isNullOrEmpty()) {
                val jsonObject = JSONObject(errorJsonString)
                jsonObject.optString("message", "Login failed: ${response.code()}")
            } else {
                "Login failed: ${response.message()}"
            }
        } catch (e: Exception) {
            "Login failed: ${response.code()}"
        }
    }
}