package com.selvaganesh7378.subtrack.domain.repository

import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.calendar.CalendarData

interface CalendarRepository {
    suspend fun getCalendarData(year: Int, page: Int): LocalResult<CalendarData>
}