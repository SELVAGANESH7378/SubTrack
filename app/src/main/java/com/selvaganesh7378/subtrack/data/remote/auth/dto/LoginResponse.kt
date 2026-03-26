package com.selvaganesh7378.subtrack.data.remote.auth.dto


data class LoginResponse(
    val message: String,
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)
