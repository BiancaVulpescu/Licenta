package com.example.drivocare.repositories

import com.example.drivocare.data.Car
import com.example.drivocare.data.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CarRepository : ICarRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun getCars(userId: String): Flow<List<Car>> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .collection("cars")
            .addSnapshotListener { snapshot, _ ->
                val cars = snapshot?.toObjects(Car::class.java) ?: emptyList()
                trySend(cars)
            }
        awaitClose { listener.remove() }
    }

    override fun getEvents(carId: String): Flow<List<Event>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null || carId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(userId)
            .collection("cars")
            .document(carId)
            .collection("events")
            .orderBy("startDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val events = snapshot?.toObjects(Event::class.java) ?: emptyList()
                trySend(events)
            }
        awaitClose { listener.remove() }
    }

    override fun addCar(car: Car, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onFailure("User not logged in")
        val doc = firestore.collection("users")
            .document(userId)
            .collection("cars")
            .document()

        val carWithId = car.copy(id = doc.id, userId = userId)

        doc.set(carWithId)
            .addOnSuccessListener { onSuccess(doc.id) }
            .addOnFailureListener { onFailure(it.message ?: "Error adding car") }
    }

    override fun updateCar(carId: String, car: Car, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onFailure("User not logged in")
        firestore.collection("users")
            .document(userId)
            .collection("cars")
            .document(carId)
            .set(car)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Error updating car") }
    }

    override fun deleteCar(carId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onFailure("User not logged in")
        firestore.collection("users")
            .document(userId)
            .collection("cars")
            .document(carId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Error deleting car") }
    }

    override fun addEvent(event: Event, carId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onFailure("User not logged in")
        if (carId.isBlank()) return onFailure("Car ID is missing")

        val eventRef = firestore.collection("users")
            .document(userId)
            .collection("cars")
            .document(carId)
            .collection("events")
            .document()

        val eventWithId = event.copy(
            id = eventRef.id,
            userId = userId,
            carId = carId
        )

        eventRef.set(eventWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Error adding event") }
    }


    override fun addCarWithEvents(
        car: Car,
        events: List<Event>,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return onFailure("User not logged in")
        val carRef = firestore.collection("users")
            .document(userId)
            .collection("cars")
            .document()

        val carWithId = car.copy(id = carRef.id, userId = userId)

        carRef.set(carWithId)
            .addOnSuccessListener {
                val batch = firestore.batch()
                events.forEach { event ->
                    val eventRef = carRef.collection("events").document()
                    val eventWithIds = event.copy(
                        id = eventRef.id,
                        userId = userId,
                        carId = carWithId.id
                    )
                    batch.set(eventRef, eventWithIds)
                }
                batch.commit()
                    .addOnSuccessListener { onSuccess(carWithId.id) }
                    .addOnFailureListener { onFailure(it.message ?: "Error saving events") }
            }
            .addOnFailureListener { onFailure(it.message ?: "Error adding car") }
    }
}
