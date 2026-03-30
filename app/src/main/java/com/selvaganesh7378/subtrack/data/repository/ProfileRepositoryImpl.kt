package com.selvaganesh7378.subtrack.data.repository

import android.content.Context
import android.net.Uri
import com.selvaganesh7378.subtrack.data.local.TokenManager
import com.selvaganesh7378.subtrack.data.local.datastore.UserDataStore
import com.selvaganesh7378.subtrack.data.remote.profile.ProfileApiService
import com.selvaganesh7378.subtrack.data.remote.profile.dto.PasswordUpdateDto
import com.selvaganesh7378.subtrack.data.remote.profile.dto.ProfileUpdateDto
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.Profile
import com.selvaganesh7378.subtrack.domain.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ProfileApiService,
    private val userDataStore: UserDataStore,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) : ProfileRepository {

    override fun getProfileStream(): Flow<Profile?> {
        return userDataStore.profileFlow
    }

    override suspend fun updateProfile(profile: Profile): LocalResult<Unit> {
        return try {
            val updateDto = ProfileUpdateDto(
                name = profile.name,
                email = profile.email,
                timezone = profile.timezone
            )

            val response = apiService.updateProfile(updateDto)

            if (response.isSuccessful) {
                userDataStore.updateNameEmailAndTimezone(
                    name = profile.name,
                    email = profile.email,
                    timezone = profile.timezone ?: ""
                )
                LocalResult.Success(Unit)
            } else {
                LocalResult.Error("Update failed: ${response.code()}")
            }
        } catch (e: Exception) {
            LocalResult.Error(e.message ?: "Update failed")
        }
    }

    override suspend fun updatePassword(
        oldPassword: String,
        newPassword: String
    ): LocalResult<Unit> {
        return try {
            val passwordDto = PasswordUpdateDto(
                oldPassword = oldPassword,
                newPassword = newPassword
            )

            val response = apiService.updatePassword(passwordDto)

            if (response.isSuccessful) {
                LocalResult.Success(Unit)
            } else {
                LocalResult.Error("Password update failed: ${response.code()}")
            }
        } catch (e: Exception) {
            LocalResult.Error(e.message ?: "Password update failed")
        }
    }

    override suspend fun deleteAccount(): LocalResult<Unit> {
        return try {
            val response = apiService.deleteAccount()
            if (response.isSuccessful) {
                userDataStore.clearUser()
                tokenManager.clearTokens()
                LocalResult.Success(Unit)
            } else {
                LocalResult.Error("Delete account failed: ${response.code()}")
            }
        } catch (e: Exception) {
            LocalResult.Error(e.message ?: "Delete account failed")
        }
    }

    override suspend fun uploadProfileImage(imageUriString: String): LocalResult<String> {
        return try {
            val uri = Uri.parse(imageUriString)
            val file = getFileFromUri(uri)

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = apiService.uploadProfileImage(body)

            if (response.isSuccessful && response.body() != null) {
                val newImageUrl = response.body()!!.url

                userDataStore.updateProfileImage(newImageUrl)
                file.delete()

                LocalResult.Success(newImageUrl)
            } else {
                LocalResult.Error("Upload failed: ${response.code()}")
            }
        } catch (e: Exception) {
            LocalResult.Error(e.message ?: "Image upload failed")
        }
    }

    // Helper: Uri → File (required for Scoped Storage on API 29+)
    private fun getFileFromUri(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open input stream for URI")

        val tempFile = File(context.cacheDir, "temp_profile_upload.jpg")
        val outputStream = FileOutputStream(tempFile)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        return tempFile
    }

}