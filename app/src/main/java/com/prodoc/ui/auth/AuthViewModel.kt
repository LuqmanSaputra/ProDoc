package com.prodoc.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.prodoc.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthState {
    LOGGED_IN,
    LOGGED_OUT
}

class AuthViewModel(private val repository: ProjectRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _authState = MutableStateFlow(
        if (repository.isUserLoggedIn()) AuthState.LOGGED_IN else AuthState.LOGGED_OUT
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun registerWithEmail(name: String, email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                repository.registerWithEmail(name, email, password)
                _uiState.value = AuthUiState(isSuccess = true)
                _authState.value = AuthState.LOGGED_IN
            } catch (e: Exception) {
                _uiState.value = AuthUiState(errorMessage = e.localizedMessage ?: "Registrasi akun baru gagal.")
            }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                repository.loginWithEmail(email, password)
                _uiState.value = AuthUiState(isSuccess = true)
                _authState.value = AuthState.LOGGED_IN
            } catch (e: Exception) {
                _uiState.value = AuthUiState(errorMessage = e.localizedMessage ?: "Email atau Password salah.")
            }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                repository.logout()
                Log.d("ProDoc", "[ViewModel] AuthViewModel.logout() -> Repository selesai dijalankan.")
                _uiState.value = AuthUiState(isSuccess = false, isLoading = false, errorMessage = null)

                _authState.value = AuthState.LOGGED_OUT
            } catch (e: Exception) {
                _uiState.value = AuthUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Proses keluar akun gagal.")
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    @Suppress("unused")
    fun resetAuthState() {
        _uiState.value = AuthUiState(
            isLoading = false,
            isSuccess = false,
            errorMessage = null
        )
        _authState.value = if (repository.isUserLoggedIn()) AuthState.LOGGED_IN else AuthState.LOGGED_OUT
    }
}

class AuthViewModelFactory(private val repository: ProjectRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}