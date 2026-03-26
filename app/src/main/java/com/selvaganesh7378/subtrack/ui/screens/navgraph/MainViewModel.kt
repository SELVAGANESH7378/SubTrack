package com.selvaganesh7378.subtrack.ui.screens.navgraph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selvaganesh7378.subtrack.data.local.TokenManager
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val logoutEvent = tokenManager.logoutEvent

    // below clear token logic can be moved to repository implementation
    fun logout() {
        viewModelScope.launch {
            val refreshToken = tokenManager.getRefreshToken()

            if (refreshToken != null) {
                val result = authRepository.logout(refreshToken)

                if (result is LocalResult.Success) {
                    tokenManager.clearTokens()
                } else {

                }
            } else {
                tokenManager.clearTokens()
            }
        }
    }
}