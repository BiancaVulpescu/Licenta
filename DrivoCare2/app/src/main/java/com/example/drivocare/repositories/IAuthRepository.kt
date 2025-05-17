package com.example.drivocare.repositories

import com.example.drivocare.data.AuthState

interface IAuthRepository {
    fun login(email: String, password: String, onResult: (AuthState) -> Unit)
    fun register(email: String, password: String, username: String, onResult: (AuthState) -> Unit)
    fun logout()
    fun updateUsername(newUsername: String, onSuccess: () -> Unit, onError: (String) -> Unit)
    fun fetchCurrentUsername(onSuccess: (String) -> Unit, onError: (String) -> Unit)
    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit)
}
