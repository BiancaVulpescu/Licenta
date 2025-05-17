package com.example.drivocare.usecase

import com.example.drivocare.data.AuthState
import com.example.drivocare.repositories.IAuthRepository

class RegisterUseCase(private val repository: IAuthRepository) {
    operator fun invoke(email: String, password: String, username: String, onResult: (AuthState) -> Unit)
    {
        if (!email.contains("@")) {
            onResult(AuthState.Error("Invalid email"))
            return
        }
        repository.register(email, password, username, onResult)
    }
}
