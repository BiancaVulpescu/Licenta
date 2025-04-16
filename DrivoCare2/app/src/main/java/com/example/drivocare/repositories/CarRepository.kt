package com.example.drivocare.repositories

import com.example.drivocare.data.Car
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CarRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun addCar(car: Car, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onError("User not logged in")
        firestore.collection("users")
            .document(userId)
            .collection("cars")
            .add(car)
            .addOnSuccessListener { docRef ->
                onSuccess(docRef.id)
            }
            .addOnFailureListener { onError(it.message ?: "Error adding car") }
    }
}

