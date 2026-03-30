package com.selvaganesh7378.subtrack.domain.model.auth

data class LoginResult(
    val message: String,
    val accessToken: String,
    val refreshToken: String,
    val user: User
)