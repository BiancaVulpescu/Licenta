package com.example.drivocare.repositories

import com.example.drivocare.data.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.MutableLiveData
import com.example.drivocare.data.User

class AuthRepository : IAuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun login(email: String, password: String, onResult: (AuthState) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            onResult(AuthState.Error("Email or password can't be empty"))
            return
        }
        onResult(AuthState.Loading)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(if (task.isSuccessful) AuthState.Authenticated else AuthState.Error(task.exception?.message ?: "Login failed"))
            }
    }

    override fun register(email: String, password: String, username: String, onResult: (AuthState) -> Unit) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            onResult(AuthState.Error("Fields can't be empty"))
            return
        }
        onResult(AuthState.Loading)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserToFirestore(userId, email, username)
                    }
                    onResult(AuthState.Authenticated)
                } else {
                    onResult(AuthState.Error(task.exception?.message ?: "Registration failed"))
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
    override fun updateUsername(newUsername: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onError("User not logged in")
        firestore.collection("users").document(userId)
            .update("username", newUsername)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to update username") }
    }
    override fun fetchCurrentUsername(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onError("User not logged in")
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                onSuccess(doc.getString("username") ?: "")
            }
            .addOnFailureListener { onError(it.message ?: "Failed to fetch username") }
    }
    override fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        val email = user?.email ?: return onError("User not logged in")
        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it.message ?: "Failed to update password") }
            }
            .addOnFailureListener {
                onError("Current password is incorrect")
            }
    }
    override fun logout() {
        auth.signOut()
    }

}
