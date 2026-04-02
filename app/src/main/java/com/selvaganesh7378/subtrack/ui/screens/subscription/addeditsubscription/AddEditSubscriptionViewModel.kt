package com.selvaganesh7378.subtrack.ui.screens.subscription.addeditsubscription

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionRequestDto
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SubscriptionFormState @RequiresApi(Build.VERSION_CODES.O) constructor(
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val serviceName: String = "",
    val cost: String = "",
    val category: String = "Entertainment",
    val currency: String = "USD",
    val billingCycle: String = "Monthly",
    val paymentMethod: String = "Credit Card",
    val startDate: LocalDate = LocalDate.now(),
    val renewalDate: LocalDate = LocalDate.now().plusMonths(1),
    val remindMe: String = "3d",
    val status: String = "Active",
    val brandColor: Long = 0xFFE50914,
    val notes: String = "",
    val saveError: String? = null,
    val saveSuccess: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AddEditSubscriptionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionFormState())
    val uiState = _uiState.asStateFlow()

    private var currentSubscriptionId: Int? = null

    init {
        val subscriptionId = savedStateHandle.get<String>("subscriptionId")
        if (subscriptionId != null && subscriptionId.isNotEmpty()) {
            val id = subscriptionId.toIntOrNull()
            if (id != null) {
                currentSubscriptionId = id
                loadSubscription(id)
            }
        }
    }

    private fun loadSubscription(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val sub = repository.getSubscriptionById(id)

            if (sub != null) {
                // Parse the hex string back to a Compose Color Long
                val colorLong = try {
                    val cleanHex = if (sub.brandColorHex.startsWith("#")) sub.brandColorHex.substring(1) else sub.brandColorHex
                    val fullHex = if (cleanHex.length == 6) "FF$cleanHex" else cleanHex
                    fullHex.toLong(16)
                } catch (e: Exception) { 0xFF7986CB }

                // Parse the date string safely
                val parsedRenewalDate = try {
                    LocalDate.parse(sub.nextRenewal, DateTimeFormatter.ISO_LOCAL_DATE)
                } catch (e: Exception) { LocalDate.now() }

                _uiState.update {
                    it.copy(
                        isEditMode = true,
                        isLoading = false,
                        serviceName = sub.serviceName,
                        cost = if (sub.cost > 0) sub.cost.toString() else "",
                        category = sub.category,
                        currency = sub.currency,
                        status = sub.status.replaceFirstChar { char -> char.uppercase() },
                        billingCycle = sub.billingCycle,
                        paymentMethod = sub.paymentMethod,
                        renewalDate = parsedRenewalDate,
                        remindMe = "${sub.remindMeIn}d",
                        brandColor = colorLong
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, saveError = "Subscription not found locally") }
            }
        }
    }

    fun saveSubscription() {
        val state = _uiState.value

        // Basic validation
        if (state.serviceName.isBlank() || state.cost.isBlank()) {
            _uiState.update { it.copy(saveError = "Name and Cost are required") }
            return
        }

        val costDouble = state.cost.toDoubleOrNull() ?: 0.0
        val remindMeInt = state.remindMe.replace("d", "").toIntOrNull() ?: 3

        // Convert Long color back to "#XXXXXX" format
        val hexColor = "#" + state.brandColor.toString(16).uppercase().takeLast(6)
        // Format Date to YYYY-MM-DD
        val formattedDate = state.renewalDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

        // Build the DTO
        val requestDto = SubscriptionRequestDto(
            serviceName = state.serviceName,
            category = state.category,
            cost = costDouble,
            status = state.status.lowercase(), // API expects lowercase "active"
            nextRenewal = formattedDate,
            remindMeIn = remindMeInt,
            billingCycle = state.billingCycle,
            paymentMethod = state.paymentMethod,
            brandColorHex = hexColor,
            currency = state.currency,
            notes = state.notes
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, saveError = null) }

            val result = if (state.isEditMode && currentSubscriptionId != null) {
                repository.updateSubscription(currentSubscriptionId!!, requestDto)
            } else {
                Log.e("subsviewmodel", "color: ${requestDto.brandColorHex}")
                repository.createSubscription(requestDto)
            }

            when (result) {
                is LocalResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                }
                is LocalResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, saveError = result.message) }
                }
                else -> Unit
            }
        }
    }

    // --- State Update Functions ---
    fun updateServiceName(name: String) = _uiState.update { it.copy(serviceName = name) }
    fun updateCost(cost: String) = _uiState.update { it.copy(cost = cost) }
    fun updateCategory(category: String) = _uiState.update { it.copy(category = category) }
    fun updateCurrency(currency: String) = _uiState.update { it.copy(currency = currency) }
    fun updateBillingCycle(cycle: String) = _uiState.update { it.copy(billingCycle = cycle) }
    fun updatePaymentMethod(method: String) = _uiState.update { it.copy(paymentMethod = method) }
    fun updateStartDate(date: LocalDate) = _uiState.update { it.copy(startDate = date) }
    fun updateRenewalDate(date: LocalDate) = _uiState.update { it.copy(renewalDate = date) }
    fun updateRemindMe(remindMe: String) = _uiState.update { it.copy(remindMe = remindMe) }
    fun updateStatus(status: String) = _uiState.update { it.copy(status = status) }
    fun updateBrandColor(color: Long) = _uiState.update { it.copy(brandColor = color) }
    fun updateNotes(notes: String) = _uiState.update { it.copy(notes = notes) }

    fun getCurrencySymbol(currencyCode: String): String {
        return when (currencyCode.uppercase()) {
            "USD" -> "$"
            "INR" -> "₹"
            "EUR" -> "€"
            "GBP" -> "£"
            "AED" -> "د.إ"
            else -> currencyCode
        }
    }

}