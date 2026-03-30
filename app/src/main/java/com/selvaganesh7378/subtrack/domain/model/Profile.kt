package com.selvaganesh7378.subtrack.domain.model

data class Profile(
    val id: Int,
    val name: String,
    val email: String,
    val photoUrl: String,
    val timezone: String,
    val createdAt: String
)
