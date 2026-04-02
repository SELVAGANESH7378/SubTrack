package com.selvaganesh7378.subtrack.ui.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription
import com.selvaganesh7378.subtrack.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubscriptionItem(
    val id: String,
    val name: String,
    val price: Double,
    val currencySymbol: String,
    val billingCycle: String,
    val status: String,
    val color: Long,
    val category: String
)

data class SubscriptionScreenState(
    val subscriptions: List<SubscriptionItem> = emptyList(),
    val filteredSubscriptions: List<SubscriptionItem> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: String = "All",
    val categoryFilter: String = "All",
    val totalActive: Int = 0,
    val monthlyCost: Double = 0.0,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        observeDatabase()
        triggerBackgroundSync()
    }

    // 1. Constantly listen to Room DB. Any changes instantly update the UI.
    private fun observeDatabase() {
        viewModelScope.launch {
            repository.getSubscriptionsStream().collect { domainSubscriptions ->
                val uiItems = domainSubscriptions.map { it.toUiModel() }

                _uiState.update {
                    it.copy(subscriptions = uiItems)
                }
                applyFilters()
            }
        }
    }

    // 2. Fetch fresh data from the API to update Room.
    private fun triggerBackgroundSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

            when (val result = repository.syncSubscriptions()) {
                is LocalResult.Success -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
                is LocalResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    // Called by the UI (e.g., Pull-to-refresh)
    fun refreshSubscriptions() {
        triggerBackgroundSync()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun updateStatusFilter(status: String) {
        _uiState.update { it.copy(statusFilter = status) }
        applyFilters()
    }

    fun updateCategoryFilter(category: String) {
        _uiState.update { it.copy(categoryFilter = category) }
        applyFilters()
    }

    fun deleteSubscription(id: String) {
        val numericId = id.toIntOrNull() ?: return

        viewModelScope.launch {
            // Show loading state and clear any previous errors
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

            when (val result = repository.deleteSubscription(numericId)) {
                is LocalResult.Success -> {
                    // Stop loading.
                    // observeDatabase() handles the list updates automatically!
                    _uiState.update { it.copy(isRefreshing = false) }
                }
                is LocalResult.Error -> {
                    // Stop loading and show the error message
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    private fun applyFilters() {
        val state = _uiState.value

        val filtered = state.subscriptions.filter { sub ->
            val matchesSearch = sub.name.contains(state.searchQuery, ignoreCase = true)
            val matchesStatus = state.statusFilter == "All" || sub.status.equals(state.statusFilter, ignoreCase = true)
            val matchesCategory = state.categoryFilter == "All" || sub.category.equals(state.categoryFilter, ignoreCase = true)

            matchesSearch && matchesStatus && matchesCategory
        }

        val activeSubs = state.subscriptions.filter { it.status.equals("Active", ignoreCase = true) }
        val totalActiveCount = activeSubs.size

        // Assuming we calculate USD for the UI based on your previous logic
        val calculatedMonthly = activeSubs.filter { it.currencySymbol == "$" }.sumOf { it.price }

        _uiState.update {
            it.copy(
                filteredSubscriptions = filtered,
                totalActive = totalActiveCount,
                monthlyCost = calculatedMonthly
            )
        }
    }

    // --- Helper Mappers ---

    /**
     * Converts the pure Domain Subscription into the SubscriptionItem used by Jetpack Compose
     */
    private fun Subscription.toUiModel(): SubscriptionItem {
        return SubscriptionItem(
            id = this.id.toString(),
            name = this.serviceName,
            price = this.cost,
            currencySymbol = getCurrencySymbol(this.currency),
            billingCycle = this.billingCycle,
            // Ensure status is capitalized nicely (e.g. "active" -> "Active")
            status = this.status.replaceFirstChar { it.uppercase() },
            color = parseHexColor(this.brandColorHex),
            category = this.category
        )
    }

    private fun getCurrencySymbol(currencyCode: String): String {
        return when (currencyCode.uppercase()) {
            "USD" -> "$"
            "INR" -> "₹"
            "EUR" -> "€"
            "GBP" -> "£"
            "AED" -> "د.إ"
            else -> currencyCode
        }
    }

    private fun parseHexColor(hexString: String): Long {
        return try {
            val cleanHex = if (hexString.startsWith("#")) hexString.substring(1) else hexString
            // Compose colors expect ARGB. If the backend just sends RGB (6 chars), append FF for full opacity.
            val fullHex = if (cleanHex.length == 6) "FF$cleanHex" else cleanHex
            fullHex.toLong(16)
        } catch (e: Exception) {
            0xFF808080
        }
    }
}