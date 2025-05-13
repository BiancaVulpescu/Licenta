package com.example.drivocare.usecase

import android.util.Patterns
import com.example.drivocare.data.AuthState
import com.example.drivocare.repositories.IAuthRepository

class LoginUseCase(private val repository: IAuthRepository) {
    operator fun invoke(email: String, password: String, onResult: (AuthState) -> Unit)
    {
        when {
            email.isBlank() || password.isBlank() -> {
                onResult(AuthState.Error("Email and password cannot be empty"))
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                onResult(AuthState.Error("Invalid email format"))
            }

            else -> {
                repository.login(email, password, onResult)
            }
        }
    }
}
