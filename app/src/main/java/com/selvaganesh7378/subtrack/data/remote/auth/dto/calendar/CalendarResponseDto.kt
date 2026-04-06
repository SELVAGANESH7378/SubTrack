package com.selvaganesh7378.subtrack.data.remote.auth.dto.calendar

import com.google.gson.annotations.SerializedName

data class CalendarResponseDto(
    @SerializedName("loaded") val loaded: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("records") val records: List<CalendarRecordDto>,
    @SerializedName("pagination") val pagination: CalendarPaginationDto,
    @SerializedName("filtereds") val filtereds: Int,
    @SerializedName("summary") val summary: CalendarSummaryDto
)
