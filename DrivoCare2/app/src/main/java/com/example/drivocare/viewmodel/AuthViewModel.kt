package com.example.drivocare.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.AuthState
import com.example.drivocare.repositories.AuthRepository

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    val authState = repository.authState
    val currentUsername = mutableStateOf("")

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
        repository.fetchCurrentUsername(
            onSuccess = { currentUsername.value = it }
        )
    }
    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.changePassword(currentPassword, newPassword, onSuccess, onError)
    }
    fun logout() {
        repository.logout()
    }
}

