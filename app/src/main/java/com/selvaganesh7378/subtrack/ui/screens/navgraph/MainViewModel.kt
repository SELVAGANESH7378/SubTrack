package com.selvaganesh7378.subtrack.ui.screens.navgraph

import androidx.lifecycle.ViewModel
import com.selvaganesh7378.subtrack.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    val logoutEvent = tokenManager.logoutEvent

    fun logout() {
        tokenManager.clearTokens()
    }
}