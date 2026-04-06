package com.selvaganesh7378.subtrack.ui.screens.calander

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription
import com.selvaganesh7378.subtrack.domain.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit // IMPORT THIS
import javax.inject.Inject

data class CalendarUiState(
    val isLoading: Boolean = false,
    val events: List<SubscriptionEvent> = emptyList(),
    val targetMonthFetched: YearMonth? = null,
    val monthTotal: String = "$0.00",
    val totalPages: Int = Int.MAX_VALUE,
    val errorMessage: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchCalendarData(YearMonth.now())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchCalendarData(targetMonth: YearMonth) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val currentMonth = YearMonth.now()
            val monthsBetween = ChronoUnit.MONTHS.between(currentMonth, targetMonth).toInt()

            val calculatedPage = monthsBetween + 1


            if (calculatedPage < 1 || calculatedPage > _uiState.value.totalPages) {
                _uiState.update {
                    it.copy(isLoading = false, events = emptyList(), targetMonthFetched = targetMonth, monthTotal = "$0.00")
                }
                return@launch
            }

            when (val result = repository.getCalendarData(year = targetMonth.year, page = calculatedPage)) {
                is LocalResult.Success -> {
                    val allSubscriptions = result.data.records.flatMap { it.subscriptions }
                    val uiEvents = allSubscriptions.map { it.toEvent() }

                    // Grab the monthly cost from the first record in the API response
                    val apiMonthTotal = result.data.records.firstOrNull()?.monthlyCost ?: "$0.00"

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            events = uiEvents,
                            targetMonthFetched = targetMonth,
                            monthTotal = apiMonthTotal
                        )
                    }
                }
                is LocalResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> Unit
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Subscription.toEvent(): SubscriptionEvent {
        val parsedDate = try {
            LocalDate.parse(this.nextRenewal, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) { LocalDate.now() }

        return SubscriptionEvent(
            id = this.id.toString(),
            name = this.serviceName,
            date = parsedDate,
            price = this.cost,
            color = Color(parseHexColor(this.brandColorHex)),
            letter = this.serviceName.take(1).uppercase(),
            category = this.category
        )
    }

    private fun parseHexColor(hexString: String): Long {
        return try {
            val cleanHex = if (hexString.startsWith("#")) hexString.substring(1) else hexString
            val fullHex = if (cleanHex.length == 6) "FF$cleanHex" else cleanHex
            fullHex.toLong(16)
        } catch (e: Exception) { 0xFF808080 }
    }
}