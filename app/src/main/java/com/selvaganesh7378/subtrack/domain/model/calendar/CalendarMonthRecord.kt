package com.selvaganesh7378.subtrack.domain.model.calendar

import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription

data class CalendarMonthRecord(
    val year: Int,
    val month: String,
    val monthlyCost: String,
    val userCurrency: String,
    val subscriptions: List<Subscription>
)