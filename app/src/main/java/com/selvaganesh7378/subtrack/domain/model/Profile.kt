package com.selvaganesh7378.subtrack.domain.model

data class Profile(
    val id: Int,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val timezone: String? = null
)
