package com.selvaganesh7378.subtrack.data.remote.auth.dto.calendar

import com.google.gson.annotations.SerializedName

data class CalendarSummaryDto(
    @SerializedName("totalActive") val totalActive: Int
)