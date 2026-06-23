package com.prodoc.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.prodoc.data.local.dao.UserDao
import com.prodoc.data.local.entity.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val userDao: UserDao) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isUserLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    fun registerWithEmail(name: String, email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val localUser = UserEntity(
                        uid = firebaseUser.uid,
                        name = name,
                        email = email,
                        createdAt = System.currentTimeMillis()
                    )
                    userDao.saveUser(localUser)
                    _uiState.value = AuthUiState(isSuccess = true)
                } else {
                    _uiState.value = AuthUiState(errorMessage = "Gagal memuat data.")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(errorMessage = e.localizedMessage ?: "Terjadi kesalahan.")
            }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val localUser = UserEntity(
                        uid = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "Teknisi ProDoc",
                        email = email,
                        createdAt = System.currentTimeMillis()
                    )
                    userDao.saveUser(localUser)
                    _uiState.value = AuthUiState(isSuccess = true)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(errorMessage = e.localizedMessage ?: "Email atau Password salah.")
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetAuthState() {
        _uiState.value = AuthUiState(
            isLoading = false,
            isSuccess = false,
            errorMessage = null
        )
    }
}

class AuthViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}