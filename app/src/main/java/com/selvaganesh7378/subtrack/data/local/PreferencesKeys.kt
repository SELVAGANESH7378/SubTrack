package com.selvaganesh7378.subtrack.data.local

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val USER_ID = intPreferencesKey("user_id")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")

    val USER_PHOTO_URL = stringPreferencesKey("user_photo_url")
    val USER_TIMEZONE = stringPreferencesKey("user_timezone")

    val CREATED_AT = stringPreferencesKey("created_at")
}