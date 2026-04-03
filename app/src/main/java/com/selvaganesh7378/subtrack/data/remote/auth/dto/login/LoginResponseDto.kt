package com.selvaganesh7378.subtrack.data.remote.auth.dto.login

import com.selvaganesh7378.subtrack.data.remote.auth.dto.UserDto


data class LoginResponseDto(
    val message: String,
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)
