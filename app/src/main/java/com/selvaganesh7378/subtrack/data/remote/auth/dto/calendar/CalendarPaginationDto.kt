package com.selvaganesh7378.subtrack.data.remote.auth.dto.calendar

import com.google.gson.annotations.SerializedName

data class CalendarPaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("hasNextPage") val hasNextPage: Boolean,
    @SerializedName("hasPrevPage") val hasPrevPage: Boolean
)