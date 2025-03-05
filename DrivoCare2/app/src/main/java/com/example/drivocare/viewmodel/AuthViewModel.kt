package com.example.drivocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivocare.data.AuthState
import com.example.drivocare.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    //val authState = repository.authStated
    private val _authState= MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState
    init{
        checkAuthStatus()
    }
    fun checkAuthStatus(){
        viewModelScope.launch{
            _authState.value=repository.authState.value ?: AuthState.Loading
        }
    }
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

