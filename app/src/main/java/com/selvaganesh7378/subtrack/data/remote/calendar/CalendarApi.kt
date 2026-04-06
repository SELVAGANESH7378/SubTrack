package com.selvaganesh7378.subtrack.data.remote.calendar

import com.selvaganesh7378.subtrack.data.remote.auth.dto.calendar.CalendarResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CalendarApi {

    @GET("subscription/calendar")
    suspend fun getCalendarData(
        @Query("year") year: Int,
        @Query("page") page: Int = 1
    ): Response<CalendarResponseDto>

}