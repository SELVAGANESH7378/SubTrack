package com.selvaganesh7378.subtrack.ui.screens.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selvaganesh7378.subtrack.data.remote.auth.dto.LoginResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.RegisterResponse
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _signUpState =  MutableStateFlow<LocalResult<RegisterResponse>?>(null)
    val signUpState = _signUpState.asStateFlow()

    fun register(name: String, email: String, psw: String) {
        viewModelScope.launch {
            _signUpState.value = LocalResult.Loading
            val currentTimezone = TimeZone.getDefault().id
            Log.d("RegisterViewModel", "Current timezone: $currentTimezone")
            val result = authRepository.signUp(
                name,
                email,
                psw,
                currentTimezone
            )
            _signUpState.value = result
        }
    }
}