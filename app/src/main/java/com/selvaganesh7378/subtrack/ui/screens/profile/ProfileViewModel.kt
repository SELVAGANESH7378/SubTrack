package com.selvaganesh7378.subtrack.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selvaganesh7378.subtrack.domain.LocalResult
import com.selvaganesh7378.subtrack.domain.model.Profile
import com.selvaganesh7378.subtrack.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val profile: Profile? = null,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = true,
    val isUploadingImage: Boolean = false,
    val isSavingProfile: Boolean = false,
    val isUpdatingPassword: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeProfile()
        triggerBackgroundSync()
    }

    fun uploadProfileImage(uriString: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingImage = true, errorMessage = null, successMessage = null) }

            val result = repository.uploadProfileImage(uriString)

            when (result) {
                is LocalResult.Success -> {
                    _uiState.update { it.copy(isUploadingImage = false, successMessage = "Profile photo updated!") }
                }
                is LocalResult.Error -> {
                    _uiState.update { it.copy(isUploadingImage = false, errorMessage = result.message) }
                }
                else -> Unit
            }
        }
    }

    private fun triggerBackgroundSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

            when (val result = repository.syncProfile()) {
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

    fun refreshProfile() {
        triggerBackgroundSync()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            repository.getProfileStream().collect { profileData ->
                _uiState.update {
                    it.copy(
                        profile = profileData,
                        isLoading = profileData == null
                    )
                }
            }
        }
    }

    fun updateProfile(name: String,email: String, timezone: String) {
        val currentProfile = _uiState.value.profile ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingProfile = true, errorMessage = null, successMessage = null) }

            val updatedProfile = currentProfile.copy(name = name, email = email, timezone = timezone)
            val result = repository.updateProfile(updatedProfile)

            when (result) {
                is LocalResult.Success -> {
                    _uiState.update { it.copy(isSavingProfile = false, successMessage = "Profile updated successfully") }
                }
                is LocalResult.Error -> {
                    _uiState.update { it.copy(isSavingProfile = false, errorMessage = result.message) }
                }
                else -> Unit
            }
        }
    }

    fun updatePassword(oldPsw: String, newPsw: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingPassword = true, errorMessage = null, successMessage = null) }

            val result = repository.updatePassword(oldPsw, newPsw)

            when (result) {
                is LocalResult.Success -> {
                    _uiState.update { it.copy(isUpdatingPassword = false, successMessage = "Password updated successfully") }
                }
                is LocalResult.Error -> {
                    _uiState.update { it.copy(isUpdatingPassword = false, errorMessage = result.message) }
                }
                else -> Unit
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, errorMessage = null) }

            val result = repository.deleteAccount()

            when (result) {
                is LocalResult.Success -> {
                    _uiState.update { it.copy(isDeletingAccount = false) }
                }
                is LocalResult.Error -> {
                    _uiState.update { it.copy(isDeletingAccount = false, errorMessage = result.message) }
                }
                else -> Unit
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}