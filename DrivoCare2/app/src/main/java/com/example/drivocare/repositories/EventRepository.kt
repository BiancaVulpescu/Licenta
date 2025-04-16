package com.example.drivocare.repositories

import com.example.drivocare.data.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EventRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun addEvent(carId: String, event: Event, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onError("User not logged in")
        firestore.collection("users")
            .document(userId)
            .collection("cars")
            .document(carId)
            .collection("events")
            .add(event)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error adding event") }
    }
}
