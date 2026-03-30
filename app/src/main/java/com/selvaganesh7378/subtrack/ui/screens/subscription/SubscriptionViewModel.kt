package com.selvaganesh7378.subtrack.ui.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    val billingCycle: String, // "mo" or "yr"
    val status: String,       // "Active" or "Cancelled"
    val color: Long,
    val category: String
)

data class SubscriptionScreenState(
    val subscriptions: List<SubscriptionItem> = emptyList(),
    val filteredSubscriptions: List<SubscriptionItem> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: String = "All", // "All", "Active", "Cancelled"
    val categoryFilter: String = "All",
    val totalActive: Int = 0,
    val monthlyCost: Double = 0.0,
    val isRefreshing: Boolean = false
)



@HiltViewModel
class SubscriptionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val mockData = listOf(
            SubscriptionItem("1", "Netflix", 649.0, "₹", "mo", "Cancelled", 0xFFE53935, "Entertainment"),
            SubscriptionItem("2", "Spotify", 9.99, "$", "mo", "Active", 0xFF4CAF50, "Entertainment"),
            SubscriptionItem("3", "AWS", 45.0, "$", "mo", "Active", 0xFFFFB74D, "Development"),
            SubscriptionItem("4", "GitHub Pro", 4.0, "$", "mo", "Active", 0xFF7986CB, "Development"),
            SubscriptionItem("5", "Figma", 12.0, "$", "mo", "Active", 0xFFF06292, "Design"),
            SubscriptionItem("6", "Notion", 8.0, "$", "mo", "Active", 0xFF7986CB, "Productivity"),
            SubscriptionItem("7", "Adobe CC", 54.99, "$", "mo", "Active", 0xFFE53935, "Design"),
            SubscriptionItem("8", "ChatGPT Plus", 20.0, "$", "mo", "Active", 0xFF81C784, "Productivity")
        )

        _uiState.update {
            it.copy(subscriptions = mockData)
        }
        applyFilters()
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
        // In a real app, delete from repository here
        _uiState.update { state ->
            state.copy(subscriptions = state.subscriptions.filter { it.id != id })
        }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value

        val filtered = state.subscriptions.filter { sub ->
            val matchesSearch = sub.name.contains(state.searchQuery, ignoreCase = true)
            val matchesStatus = state.statusFilter == "All" || sub.status == state.statusFilter
            val matchesCategory = state.categoryFilter == "All" || sub.category == state.categoryFilter

            matchesSearch && matchesStatus && matchesCategory
        }

        val activeSubs = state.subscriptions.filter { it.status == "Active" }
        val totalActiveCount = activeSubs.size
        // Rough estimate for monthly cost mapping (Assuming all active USD items for the dummy $149.23 calculation)
        val calculatedMonthly = activeSubs.filter { it.currencySymbol == "$" }.sumOf { it.price }

        _uiState.update {
            it.copy(
                filteredSubscriptions = filtered,
                totalActive = totalActiveCount,
                monthlyCost = calculatedMonthly // e.g. 149.23
            )
        }
    }

    fun refreshSubscriptions() {
        viewModelScope.launch {
            // 1. Tell the UI to show the loading spinner
            _uiState.update { it.copy(isRefreshing = true) }

            // 2. Simulate a network/database delay (remove this in production!)
            delay(1200)

            // 3. Fetch the fresh data from your repository
            loadMockData() // In production: repository.getAllSubscriptions()

            // 4. Tell the UI to hide the loading spinner
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}