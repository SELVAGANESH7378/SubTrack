package com.selvaganesh7378.subtrack.ui.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription
import com.selvaganesh7378.subtrack.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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
    val searchQuery: String = "",
    val statusFilter: String = "All",
    val categoryFilter: String = "All",
    val totalActive: Int = 0,
    val monthlyCost: String = "$0.00",
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    init {
        // 2. Observe the single summary table
        viewModelScope.launch {
            repository.getSubscriptionSummary().collect { summary ->
                if (summary != null) {
                    _uiState.update {
                        it.copy(
                            totalActive = summary.totalActive,
                            monthlyCost = summary.monthlyCost
                        )
                    }
                }
            }
        }
    }
    private val _uiState = MutableStateFlow(SubscriptionScreenState())
    val uiState = _uiState.asStateFlow()

    // Whenever searchQuery, statusFilter, or categoryFilter changes,
    // flatMapLatest triggers a NEW Pager, which forces the RemoteMediator to hit the API!
    @OptIn(ExperimentalCoroutinesApi::class)
    val subscriptionsPagingFlow = _uiState
        .map { Triple(it.searchQuery, it.statusFilter, it.categoryFilter) }
        .distinctUntilChanged()
        .flatMapLatest { (query, status, category) ->
            repository.getSubscriptionsStream(query, status, category).map { pagingData ->
                pagingData.map { it.toUiModel() }
            }
        }
        .cachedIn(viewModelScope)


    fun updateSearchQuery(query: String) = _uiState.update { it.copy(searchQuery = query) }
    fun updateStatusFilter(status: String) = _uiState.update { it.copy(statusFilter = status) }
    fun updateCategoryFilter(category: String) = _uiState.update { it.copy(categoryFilter = category) }

    fun deleteSubscription(id: String) {
        val numericId = id.toIntOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            when (val result = repository.deleteSubscription(numericId)) {
                is LocalResult.Success -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
                is LocalResult.Error -> {
                    _uiState.update { it.copy(isRefreshing = false, errorMessage = result.message) }
                }
                else -> Unit
            }
        }
    }

    private fun Subscription.toUiModel(): SubscriptionItem {
        return SubscriptionItem(
            id = this.id.toString(),
            name = this.serviceName,
            price = this.cost,
            currencySymbol = getCurrencySymbol(this.currency),
            billingCycle = this.billingCycle,
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
            val fullHex = if (cleanHex.length == 6) "FF$cleanHex" else cleanHex
            fullHex.toLong(16)
        } catch (e: Exception) {
            0xFF808080
        }
    }
}