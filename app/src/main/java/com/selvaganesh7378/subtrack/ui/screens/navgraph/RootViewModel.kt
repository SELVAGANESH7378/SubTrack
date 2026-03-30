package com.selvaganesh7378.subtrack.ui.screens.navgraph

import androidx.lifecycle.ViewModel
import com.selvaganesh7378.subtrack.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {
    // global logout event
    val logoutEvent = tokenManager.logoutEvent
}