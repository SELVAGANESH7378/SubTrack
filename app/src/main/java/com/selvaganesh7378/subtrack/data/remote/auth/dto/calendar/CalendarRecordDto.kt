package com.selvaganesh7378.subtrack.data.remote.auth.dto.calendar

import com.google.gson.annotations.SerializedName
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionDto

data class CalendarRecordDto(
    @SerializedName("year") val year: Int,
    @SerializedName("month") val month: String,
    @SerializedName("monthlyCost") val monthlyCost: String,
    @SerializedName("userCurrency") val userCurrency: String,
    @SerializedName("data") val data: List<SubscriptionDto> // Reusing your existing SubscriptionDto
)