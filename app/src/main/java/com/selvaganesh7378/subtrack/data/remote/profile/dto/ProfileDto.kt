package com.selvaganesh7378.subtrack.data.remote.profile.dto

data class ProfileDto(
    val name: String,
    val email: String,
    val photoUrl: String,
    val timezone: String?,
    val createdAt: String,
)
