package com.selvaganesh7378.subtrack.domain.model.calendar

data class CalendarData(
    val records: List<CalendarMonthRecord>,
    val hasNextPage: Boolean,
    val currentPage: Int,
    val totalPages: Int
)
