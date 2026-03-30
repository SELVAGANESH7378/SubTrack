package com.selvaganesh7378.subtrack.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.selvaganesh7378.subtrack.data.local.PreferencesKeys.USER_EMAIL
import com.selvaganesh7378.subtrack.data.local.PreferencesKeys.USER_ID
import com.selvaganesh7378.subtrack.data.local.PreferencesKeys.USER_NAME
import com.selvaganesh7378.subtrack.data.local.PreferencesKeys.USER_PHOTO_URL
import com.selvaganesh7378.subtrack.data.local.PreferencesKeys.USER_TIMEZONE
import com.selvaganesh7378.subtrack.domain.model.Profile
import com.selvaganesh7378.subtrack.domain.model.auth.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {


    suspend fun saveUserLogin(user: User) {
        context.userDataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USER_NAME] = user.name
            preferences[USER_EMAIL] = user.email
            preferences[USER_PHOTO_URL] = user.email
            preferences[USER_TIMEZONE] = user.email
        }
    }

    suspend fun saveUserProfile(profile: Profile) {
        context.userDataStore.edit { preferences ->
            preferences[USER_ID] = profile.id
            preferences[USER_NAME] = profile.name
            preferences[USER_EMAIL] = profile.email
            preferences[USER_PHOTO_URL] = profile.photoUrl
            preferences[USER_TIMEZONE] = profile.timezone
        }
    }

    suspend fun saveTimeZone(timeZone: String) {
        context.userDataStore.edit { preferences ->
            preferences[USER_TIMEZONE] = timeZone
        }
    }

    suspend fun updateNameEmailAndTimezone(name: String,email: String, timezone: String) {
        context.userDataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
            preferences[USER_TIMEZONE] = timezone
        }
    }

    suspend fun updateProfileImage(photoUrl: String) {
        context.userDataStore.edit { preferences ->
            preferences[USER_PHOTO_URL] = photoUrl
        }
    }



    val profileFlow: Flow<Profile?> = context.userDataStore.data.map { preferences ->
        val id = preferences[USER_ID]
        val name = preferences[USER_NAME]
        val email = preferences[USER_EMAIL]
        val photoUrl = preferences[USER_PHOTO_URL]
        val timezone = preferences[USER_TIMEZONE]


        if (id != null && name != null && email != null && photoUrl != null && timezone != null) {
            Profile(
                id = id,
                name = name,
                email = email,
                photoUrl = photoUrl,
                timezone = timezone,
                createdAt = TODO()
            )
        } else {
            null
        }
    }

    suspend fun clearUser() {
        context.userDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}