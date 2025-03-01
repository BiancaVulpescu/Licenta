package com.example.drivocare.data.repositories

import com.example.drivocare.data.models.User
import com.google.firebase.auth.FirebaseAuth

class AuthRepository(private val userRepository: UserRepository) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun checkAuthStatus(callback: (Boolean) -> Unit) {
        callback(auth.currentUser != null)
    }

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            callback(false, "Email or password can't be empty")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun register(email: String, password: String, username: String, callback: (Boolean, String?) -> Unit) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            callback(false, "Fields can't be empty")
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val user = User(userId, email, username)
                        userRepository.saveUserToFirestore(user) { success, errorMessage ->
                            if (success) {
                                callback(true, null)
                            } else {
                                callback(false, errorMessage)
                            }
                        }
                    } else {
                        callback(false, "Failed to get user ID")
                    }
                } else {
                    callback(false, task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}
