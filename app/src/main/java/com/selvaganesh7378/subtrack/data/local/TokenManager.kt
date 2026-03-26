package com.selvaganesh7378.subtrack.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    // This creates an encrypted file that hackers/rooted devices cannot easily read

    private val _logoutEvent = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val logoutEvent = _logoutEvent.asSharedFlow()
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString("jwt_access_token", accessToken)
            .putString("jwt_refresh_token", refreshToken)
            .apply() // .apply() saves it asynchronously in the background
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("jwt_access_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("jwt_refresh_token", null)
    }


    fun clearTokens() {
        sharedPreferences.edit().clear().apply()
        _logoutEvent.tryEmit(Unit)
    }
}