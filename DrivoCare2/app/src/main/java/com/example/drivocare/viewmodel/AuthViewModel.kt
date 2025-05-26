package com.example.drivocare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivocare.data.AuthState
import com.example.drivocare.repositories.IAuthRepository
import com.example.drivocare.usecase.LoginUseCase
import com.example.drivocare.usecase.RegisterUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel (
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val repository: IAuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUsername = MutableStateFlow("Username")
    val currentUsername: StateFlow<String> = _currentUsername


    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        _authState.value = if (isLoggedIn) {
            fetchCurrentUsername()
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        loginUseCase(email, password) {
            _authState.value = it
            if (it is AuthState.Authenticated) {
                fetchCurrentUsername()
            }
        }
    }

    fun register(email: String, password: String, username: String) {
        registerUseCase(email, password, username) {
            _authState.value = it
            if (it is AuthState.Authenticated) {
                fetchCurrentUsername()
            }
        }
    }
    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _authState.value = AuthState.Unauthenticated
        _currentUsername.value = "Username"
    }

    fun updateUsername(newUsername: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.updateUsername(newUsername, onSuccess, onError)
    }
    fun fetchCurrentUsername() {
        repository.fetchCurrentUsername(
            onSuccess = { username ->
                _currentUsername.value = if (username.isNotBlank()) username else "Username"
            },
            onError = {
                _currentUsername.value = "Username"
            }
        )
    }
    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.changePassword(currentPassword, newPassword, onSuccess, onError)
    }
    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.sendPasswordResetEmail(email, onSuccess, onError)
    }
}

