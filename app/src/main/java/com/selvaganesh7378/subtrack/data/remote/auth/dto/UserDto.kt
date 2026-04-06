package com.selvaganesh7378.subtrack.data.remote.auth.dto

data class UserDto(
    val uid: Int,
    val name: String,
    val email: String,
    val timezone: String,
    val img: String?,
    val currency: String,
    val createdAt: String,
)
