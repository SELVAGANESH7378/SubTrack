package com.selvaganesh7378.subtrack.domain.model.auth

data class User(
    val id: Int,
    val name: String = "",
    val email: String = "",
    val url: String = "",
    val timeZone: String = "",
)