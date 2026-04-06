package com.selvaganesh7378.subtrack.data.local.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.selvaganesh7378.subtrack.data.remote.auth.dto.UserDto
import com.selvaganesh7378.subtrack.domain.model.profile.Profile
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

    suspend fun saveUserProfile(profile: Profile) {
        Log.e("datastore", "profileId = ${profile.id} \n profileName = ${profile.name} " +
                "profileEmail = ${profile.email} \n profilePhotoUrl = ${profile.photoUrl} " + "" +
                "profileTimeZone = ${profile.timezone} \n profileCreatedAt = ${profile.createdAt}")

        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = profile.id
            preferences[PreferencesKeys.USER_NAME] = profile.name
            preferences[PreferencesKeys.USER_EMAIL] = profile.email
            preferences[PreferencesKeys.USER_PHOTO_URL] = profile.photoUrl ?: ""
            preferences[PreferencesKeys.USER_TIMEZONE] = profile.timezone
            preferences[PreferencesKeys.USER_CREATED_AT] = profile.createdAt
            preferences[PreferencesKeys.USER_CURRENCY] = profile.currency
        }
    }

    suspend fun saveTimeZone(timeZone: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_TIMEZONE] = timeZone
        }
    }

    suspend fun updateNameEmailAndTimezone(name: String,email: String, timezone: String, currency: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.USER_TIMEZONE] = timezone
            preferences[PreferencesKeys.USER_CURRENCY] = currency
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
        val photoUrl = preferences[PreferencesKeys.USER_PHOTO_URL]
        val timeZone = preferences[PreferencesKeys.USER_TIMEZONE]
        val createdAt = preferences[PreferencesKeys.USER_CREATED_AT]
        val currency = preferences[PreferencesKeys.USER_CURRENCY]

        if (id != null && name != null && email != null  && timeZone != null && createdAt != null && currency != null) {
            Profile(
                id = id,
                name = name,
                email = email,
                photoUrl = photoUrl,
                timezone = timeZone,
                createdAt = createdAt,
                currency = currency
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