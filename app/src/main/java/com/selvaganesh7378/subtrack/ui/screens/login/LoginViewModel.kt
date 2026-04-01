package com.selvaganesh7378.subtrack.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selvaganesh7378.subtrack.data.remote.auth.dto.login.LoginResponse
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _loginState =  MutableStateFlow<LocalResult<LoginResponse>?>(null)
    val loginState = _loginState.asStateFlow()

    fun login(email: String, psw: String) {
        viewModelScope.launch {
            _loginState.value = LocalResult.Loading
            val result = authRepository.signIn(email, psw)
            _loginState.value = result
        }
    }
    fun resetLoginState() {
        _loginState.value = null
    }
}