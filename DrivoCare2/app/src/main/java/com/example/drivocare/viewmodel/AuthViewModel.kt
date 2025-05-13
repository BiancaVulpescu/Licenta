package com.example.drivocare.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivocare.data.AuthState
import com.example.drivocare.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    val authState = repository.authState

    private val _currentUsername = MutableStateFlow<String?>(null)
    val currentUsername: StateFlow<String?> = _currentUsername

    fun login(email: String, password: String) {
        repository.login(email, password)
    }

    fun register(email: String, password: String, username: String) {
        repository.register(email, password, username)
    }
    fun updateUsername(newUsername: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.updateUsername(newUsername, onSuccess, onError)
    }
    fun fetchCurrentUsername() {
            repository.fetchCurrentUsername (
                onSuccess = { username ->
                    _currentUsername.value = username ?: "Username"
                },
                onError = { error ->
                    _currentUsername.value = "Username"
                    Log.e("AuthViewModel", "Failed to fetch username: $error")
                }
            )
    }
    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.changePassword(currentPassword, newPassword, onSuccess, onError)
    }
    fun logout() {
        repository.logout()
        _currentUsername.value = null
    }
}

