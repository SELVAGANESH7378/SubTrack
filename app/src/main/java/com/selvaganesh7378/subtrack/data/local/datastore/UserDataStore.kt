package com.selvaganesh7378.subtrack.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.selvaganesh7378.subtrack.data.remote.auth.dto.UserDto
import com.selvaganesh7378.subtrack.domain.model.Profile
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


    suspend fun saveUserLogin(user: UserDto) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = user.id
            preferences[PreferencesKeys.USER_NAME] = user.name
            preferences[PreferencesKeys.USER_EMAIL] = user.email
        }
    }

    suspend fun saveUserProfile(profile: Profile) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = profile.id
            preferences[PreferencesKeys.USER_NAME] = profile.name
            preferences[PreferencesKeys.USER_EMAIL] = profile.email
            preferences[PreferencesKeys.USER_PHOTO_URL] = profile.photoUrl ?: ""
            preferences[PreferencesKeys.USER_TIMEZONE] = profile.timezone ?: ""
        }
    }

    suspend fun saveTimeZone(timeZone: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_TIMEZONE] = timeZone
        }
    }

    suspend fun updateNameEmailAndTimezone(name: String,email: String, timezone: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.USER_TIMEZONE] = timezone
        }
    }

    suspend fun updateProfileImage(photoUrl: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_PHOTO_URL] = photoUrl
        }
    }



    val profileFlow: Flow<Profile?> = context.userDataStore.data.map { preferences ->
        val id = preferences[PreferencesKeys.USER_ID]
        val name = preferences[PreferencesKeys.USER_NAME]
        val email = preferences[PreferencesKeys.USER_EMAIL]

        if (id != null && name != null && email != null) {
            Profile(
                id = id,
                name = name,
                email = email,
                photoUrl = preferences[PreferencesKeys.USER_PHOTO_URL],
                timezone = preferences[PreferencesKeys.USER_TIMEZONE]
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