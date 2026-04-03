package com.selvaganesh7378.subtrack.data.remote.subscription.dto

data class PaginationDto(
    val page: Int? = null,
    val limit: Int? = null,
    val totalItems: Int? = null,
    val totalPages: Int? = null,
    val hasNextPage: Boolean? = null,
    val hasPrevPage: Boolean? = null,
    val source: String? = null,
    val status: String? = null,
    val category: String? = null,
    val serviceName: String? = null
)
