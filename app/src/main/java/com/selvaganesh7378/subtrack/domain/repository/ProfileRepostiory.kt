package com.selvaganesh7378.subtrack.domain.repository

import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun getProfileStream(): Flow<Profile?>

    suspend fun syncProfile(): LocalResult<Unit>

    suspend fun updateProfile(profile: Profile): LocalResult<Unit>

    suspend fun updatePassword(oldPassword: String, newPassword: String): LocalResult<Unit>

    suspend fun deleteAccount(): LocalResult<Unit>

    suspend fun uploadProfileImage(imageUriString: String): LocalResult<String>
}