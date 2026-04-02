package com.selvaganesh7378.subtrack.data.remote.auth

import com.selvaganesh7378.subtrack.data.local.TokenManager
import com.selvaganesh7378.subtrack.data.remote.auth.dto.refreshtoken.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiProvider: Provider<AuthApi>
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            tokenManager.clearTokens()
            return null
        }

        val refreshToken = tokenManager.getRefreshToken() ?: return null

        val newTokensResponse = runBlocking {
            try {
                val requestBody = RefreshTokenRequest(refreshToken = refreshToken)
                authApiProvider.get().refreshToken(requestBody)
            } catch (e: Exception) {
                null
            }
        }
        if (newTokensResponse != null && newTokensResponse.isSuccessful) {
            val body = newTokensResponse.body()
            if (body != null) {
                tokenManager.saveTokens(
                    accessToken = body.accessToken,
                    refreshToken = refreshToken // <-- Reusing the existing one!
                )

                // Clone the original failed request, but stamp the new token on it
                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${body.accessToken}")
                    .build()
            }
        }

        // 5. If we reach here, the refresh failed. Clear tokens and return null (fails the request).
        tokenManager.clearTokens()
        return null
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}