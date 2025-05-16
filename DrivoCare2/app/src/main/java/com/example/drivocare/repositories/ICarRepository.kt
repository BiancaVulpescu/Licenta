package com.example.drivocare.repositories

import com.example.drivocare.data.Car
import com.example.drivocare.data.Event
import kotlinx.coroutines.flow.Flow

interface ICarRepository {
    fun getCars(userId: String): Flow<List<Car>>
    fun getEvents(carId: String): Flow<List<Event>>
    fun addCar(car: Car, onSuccess: (String) -> Unit, onFailure: (String) -> Unit)
    fun addCarWithEvents(car: Car, events: List<Event>, onSuccess: (String) -> Unit, onFailure: (String) -> Unit)
    fun updateCar(carId: String, car: Car, onSuccess: () -> Unit, onFailure: (String) -> Unit)
    fun deleteCar(carId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit)
    fun addEvent(event: Event, carId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit)
}
