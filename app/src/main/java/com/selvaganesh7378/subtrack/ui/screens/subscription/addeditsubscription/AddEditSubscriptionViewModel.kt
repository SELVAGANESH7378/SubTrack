package com.selvaganesh7378.subtrack.ui.screens.subscription.addeditsubscription

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
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
    val brandColor: Long = 0xFF7986CB,
    val notes: String = ""
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AddEditSubscriptionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    // private val repository: SubscriptionRepository // Inject your repository here later
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionFormState())
    val uiState = _uiState.asStateFlow()

    init {
        // Automatically check if an ID was passed in navigation
        val subscriptionId = savedStateHandle.get<String>("subscriptionId")
        if (subscriptionId != null) {
            loadSubscription(subscriptionId)
        }
    }

    private fun loadSubscription(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // MOCK DATA: Replace this with `repository.getSubscriptionById(id)`
            // val sub = repository.getSubscriptionById(id)

            _uiState.update {
                it.copy(
                    isEditMode = true,
                    isLoading = false,
                    serviceName = "Netflix", // e.g., sub.name
                    cost = "649",            // e.g., sub.cost.toString()
                    category = "Entertainment",
                    currency = "INR",
                    brandColor = 0xFFE53935
                    // Map the rest of your DB fields here
                )
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

    fun saveSubscription() {
        if (_uiState.value.isEditMode) {
            // repository.updateSubscription(...)
        } else {
            // repository.insertSubscription(...)
        }
    }
}