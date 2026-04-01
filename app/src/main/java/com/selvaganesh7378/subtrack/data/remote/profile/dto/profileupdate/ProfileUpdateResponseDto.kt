package com.selvaganesh7378.subtrack.data.remote.profile.dto.profileupdate

import com.selvaganesh7378.subtrack.data.remote.auth.dto.UserDto

data class ProfileUpdateResponseDto(
    val message: String,
    val user: ProfileUpdateResponseUserDto
)
