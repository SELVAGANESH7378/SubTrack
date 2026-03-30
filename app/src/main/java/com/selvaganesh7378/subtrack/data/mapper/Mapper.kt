package com.selvaganesh7378.subtrack.data.mapper

import com.selvaganesh7378.subtrack.data.remote.auth.dto.UserDto
import com.selvaganesh7378.subtrack.data.remote.profile.dto.ProfileDto
import com.selvaganesh7378.subtrack.domain.model.Profile
import com.selvaganesh7378.subtrack.ui.screens.Screen

fun ProfileDto.toDomain(id: Int): Profile {
    return Profile(
        id = id,
        name = name,
        email = email,
        photoUrl = photoUrl,
        timezone = timezone
    )
}