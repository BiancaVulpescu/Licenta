package com.example.drivocare.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.Car
import com.example.drivocare.data.Event
import com.example.drivocare.repositories.CarRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddCarViewModel : ViewModel() {
    private val repository = CarRepository()
    var brand = mutableStateOf("")
    var year = mutableStateOf("")
    var model = mutableStateOf("")
    var number = mutableStateOf("")
    var isEditMode = mutableStateOf(false)
    var editingCarId: String? = null

    val pendingEvents = mutableStateListOf<Event>()
    fun addPendingEvent(event: Event) {
        pendingEvents.add(event)
    }

    fun saveCar(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val yearInt = year.value.toIntOrNull()
        if (yearInt == null) {
            onError("Year must be a valid number")
            return
        }

        val car = Car(brand.value, yearInt, model.value, number.value)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val carRef = FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .collection("cars")

        if (isEditMode.value && editingCarId != null) {
            carRef.document(editingCarId!!).set(car)
                .addOnSuccessListener {
                    onSuccess(editingCarId!!)
                }
                .addOnFailureListener {
                    onError(it.message ?: "Failed to edit car information")
                }
        } else {
            repository.addCar(car, onSuccess = { carId ->
                val docRef = carRef.document(carId)
                pendingEvents.forEach { event ->
                    docRef.collection("events").add(event)
                        .addOnFailureListener { Log.e("SaveCar", "Failed to save event: ${it.message}") }
                }
                onSuccess(carId)
            }, onError = onError)
        }
    }
    fun reset() {
        brand.value = ""
        model.value = ""
        year.value = ""
        number.value = ""
        isEditMode.value = false
        editingCarId = null
        pendingEvents.clear()
    }

}
