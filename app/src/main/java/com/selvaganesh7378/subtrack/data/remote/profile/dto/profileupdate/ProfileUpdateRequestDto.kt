package com.selvaganesh7378.subtrack.data.remote.profile.dto.profileupdate

data class ProfileUpdateRequestDto(
    val name: String,
    val email: String,
    val timezone: String,
    val currency: String,
)
