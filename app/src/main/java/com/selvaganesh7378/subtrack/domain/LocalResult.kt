package com.selvaganesh7378.subtrack.domain

sealed class LocalResult<out T> {

    data class Success<out T>(
        val data: T
    ) : LocalResult<T>()

    data class Error(
        val message: String
    ) : LocalResult<Nothing>()

    object Loading : LocalResult<Nothing>()
}