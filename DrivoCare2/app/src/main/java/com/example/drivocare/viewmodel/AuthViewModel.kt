package com.example.drivocare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivocare.data.AuthState
import com.example.drivocare.repositories.AuthRepository

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    val authState = repository.authState

    fun login(email: String, password: String) {
        repository.login(email, password)
    }

    fun register(email: String, password: String, username: String) {
        repository.register(email, password, username)
    }

    fun logout() {
        repository.logout()
    }
}

