package com.example.drivocare.repositories

import com.example.drivocare.data.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.MutableLiveData
import com.example.drivocare.data.User

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val authState = MutableLiveData<AuthState>()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        authState.value = if (auth.currentUser == null) AuthState.Unauthenticated else AuthState.Authenticated
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                authState.value = if (task.isSuccessful) AuthState.Authenticated else AuthState.Error(task.exception?.message ?: "Something went wrong")
            }
    }

    fun register(email: String, password: String, username: String) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            authState.value = AuthState.Error("Fields can't be empty")
            return
        }
        authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserToFirestore(userId, email, username)
                    }
                    authState.value = AuthState.Authenticated
                } else {
                    authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun saveUserToFirestore(userId: String, email: String, username: String) {
        val user = User(userId, username, email)
        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                println("User added to Firestore")
            }
            .addOnFailureListener { e ->
                println("Error adding user: ${e.message}")
            }
    }


    fun logout() {
        auth.signOut()
        authState.value = AuthState.Unauthenticated
    }
}
