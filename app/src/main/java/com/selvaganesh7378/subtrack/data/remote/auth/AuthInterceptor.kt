package com.selvaganesh7378.subtrack.data.remote.auth

import com.selvaganesh7378.subtrack.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val path = request.url.encodedPath

        val noAuthEndpoints = listOf("register", "login")

        if (noAuthEndpoints.any { path.contains(it, ignoreCase = true) }) {
            return chain.proceed(request) // Proceed without adding token
        }

        val requestBuilder = chain.request().newBuilder()

        val token = tokenManager.getAccessToken()

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        //  modified request
        return chain.proceed(requestBuilder.build())
    }
}