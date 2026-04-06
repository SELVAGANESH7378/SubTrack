package com.selvaganesh7378.subtrack.data.repository

import com.selvaganesh7378.subtrack.data.mapper.toDomain
import com.selvaganesh7378.subtrack.data.remote.calendar.CalendarApi
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.calendar.CalendarData
import com.selvaganesh7378.subtrack.domain.repository.CalendarRepository
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class CalendarRepositoryImpl @Inject constructor(
    private val api: CalendarApi
) : CalendarRepository {

    override suspend fun getCalendarData(year: Int, page: Int): LocalResult<CalendarData> {
        return try {
            val response = api.getCalendarData(year = year, page = page)

            if (response.isSuccessful && response.body() != null) {
                LocalResult.Success(response.body()!!.toDomain())
            } else {
                LocalResult.Error("Failed to fetch calendar data: ${response.code()}")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            LocalResult.Error("Network error. Please check your connection.")
        } catch (e: Exception) {
            LocalResult.Error(e.message ?: "Unexpected error occurred")
        }
    }
}