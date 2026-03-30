package com.selvaganesh7378.subtrack.data.mapper

import com.selvaganesh7378.subtrack.data.remote.auth.dto.LogOutResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.LoginResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.RegisterResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.UserDto
import com.selvaganesh7378.subtrack.domain.model.auth.LogOutResult
import com.selvaganesh7378.subtrack.domain.model.auth.LoginResult
import com.selvaganesh7378.subtrack.domain.model.auth.RegisterResult
import com.selvaganesh7378.subtrack.domain.model.auth.User

fun LoginResponse.toDomain(): LoginResult {
    return LoginResult(
        message = this.message,
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        user = this.user.toDomain()
    )
}

fun RegisterResponse.toDomain(): RegisterResult {
    return RegisterResult(
        message = this.message,
        data = this.data
    )
}

fun LogOutResponse.toDomain(): LogOutResult {
    return LogOutResult(
        message = this.message
    )
}

fun UserDto.toDomain(): User {
    return User(
         id = this.id,
         name = this.name,
         email = this.email,
        url = this.photoUrl,
        timeZone = this.timezone
    )
}
