package com.selvaganesh7378.subtrack.data.remote.auth.dto.register

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val timezone: String
)
