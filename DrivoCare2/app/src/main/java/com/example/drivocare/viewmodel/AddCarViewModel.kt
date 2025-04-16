package com.example.drivocare.viewmodel

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
        val car = Car(brand.value, yearInt , model.value, number.value)
        repository.addCar(car, onSuccess = { carId ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@addCar
            val carRef = FirebaseFirestore.getInstance()
                .collection("users").document(userId)
                .collection("cars").document(carId)

            pendingEvents.forEach { event ->
                carRef.collection("events").add(event)
            }

            onSuccess(carId)
        }, onError = onError)
    }
}
