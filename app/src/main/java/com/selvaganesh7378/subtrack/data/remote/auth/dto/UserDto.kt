package com.selvaganesh7378.subtrack.data.remote.auth.dto

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val photoUrl: String,
    val timezone: String,
    val createdAt: String,
)
