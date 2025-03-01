package com.example.drivocare.data.repositories

import com.example.drivocare.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun saveUserToFirestore(user: User, callback: (Boolean, String?) -> Unit) {
        firestore.collection("users").document(user.userId)
            .set(user)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Error saving user")
            }
    }

    fun getUserDetails(callback: (User?, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(null, "User not logged in")
            return
        }

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(user, null)
                } else {
                    callback(null, "User not found")
                }
            }
            .addOnFailureListener { e ->
                callback(null, e.message ?: "Error fetching user")
            }
    }
}
