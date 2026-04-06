package com.selvaganesh7378.subtrack.ui.screens.navgraph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.util.CoilUtils.result
import com.selvaganesh7378.subtrack.data.local.TokenManager
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.repository.AuthRepository
import com.selvaganesh7378.subtrack.domain.repository.ProfileRepository
import com.selvaganesh7378.subtrack.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class NotificationUiModel(
    val id: Int,
    val title: String,
    val description: String,
    val isRead: Boolean = false
)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _isOffline = MutableStateFlow(false)
    val isOffline = _isOffline.asStateFlow()

    val notifications = subscriptionRepository.getNotificationsFlow()
        .map { list ->
            list.map { entity ->
                NotificationUiModel(entity.id, entity.title, entity.description, entity.isRead)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val profilePhotoUrl = profileRepository.getProfileStream()
        .map { profile -> profile?.photoUrl }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            when (subscriptionRepository.syncNotifications()) {
                is LocalResult.Success -> _isOffline.value = false
                is LocalResult.Error -> _isOffline.value = true
                else -> { }
            }
        }
    }

    fun markAsRead(id: Int) {
        viewModelScope.launch { subscriptionRepository.markNotificationAsRead(id) }
    }

    fun markAllAsRead() {
        viewModelScope.launch { subscriptionRepository.markAllNotificationsAsRead() }
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


    fun logout() {
        viewModelScope.launch {
            val refreshToken = tokenManager.getRefreshToken()
            authRepository.logout(refreshToken!!)
        }
    }
}