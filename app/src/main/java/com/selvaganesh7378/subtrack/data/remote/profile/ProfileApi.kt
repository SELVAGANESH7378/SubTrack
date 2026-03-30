package com.selvaganesh7378.subtrack.data.remote.profile

import com.selvaganesh7378.subtrack.data.remote.profile.dto.ImageUploadResponseDto
import com.selvaganesh7378.subtrack.data.remote.profile.dto.PasswordUpdateDto
import com.selvaganesh7378.subtrack.data.remote.profile.dto.ProfileDto
import com.selvaganesh7378.subtrack.data.remote.profile.dto.ProfileUpdateDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileApiService {

    @GET("user/profile")
    suspend fun getProfile(): Response<ProfileDto>

    @PUT("user/updates/details")
    suspend fun updateProfile(
        @Body profileUpdate: ProfileUpdateDto
    ): Response<ProfileDto>

    @Multipart
    @PUT("user/store/img")
    suspend fun uploadProfileImage(
        @Part image: MultipartBody.Part
    ): Response<ImageUploadResponseDto>

    @PUT("user/updates/password")
    suspend fun updatePassword(
        @Body passwordData: PasswordUpdateDto
    ): Response<Unit>

    @DELETE("user/delete")
    suspend fun deleteAccount(): Response<Unit>
}