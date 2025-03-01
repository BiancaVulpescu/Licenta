package com.example.drivocare.controllers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.drivocare.data.repositories.AuthRepository
import com.example.drivocare.data.repositories.UserRepository
import com.example.drivocare.data.models.User
import com.example.drivocare.viewmodels.AuthState

class AuthController(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        authRepository.checkAuthStatus { isAuthenticated ->
            if (isAuthenticated) {
                _authState.value = AuthState.Authenticated
                fetchUserDetails()
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        authRepository.login(email, password) { success, errorMessage ->
            if (success) {
                _authState.value = AuthState.Authenticated
                fetchUserDetails()
            } else {
                _authState.value = AuthState.Error(errorMessage ?: "Something went wrong")
            }
        }
    }

    fun register(email: String, password: String, username: String) {
        _authState.value = AuthState.Loading
        authRepository.register(email, password, username) { success, errorMessage ->
            if (success) {
                _authState.value = AuthState.Authenticated
                fetchUserDetails()
            } else {
                _authState.value = AuthState.Error(errorMessage ?: "Something went wrong")
            }
        }
    }

    fun fetchUserDetails() {
        userRepository.getUserDetails { user, errorMessage ->
            if (user != null) {
                _user.value = user
            } else {
                _authState.value = AuthState.Error(errorMessage ?: "Error fetching user details")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Unauthenticated
        _user.value = null
    }
}
