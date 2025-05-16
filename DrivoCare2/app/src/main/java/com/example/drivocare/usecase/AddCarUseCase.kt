package com.example.drivocare.usecase

import com.example.drivocare.data.Car
import com.example.drivocare.repositories.ICarRepository
import com.example.drivocare.data.Event

class AddCarUseCase(private val repo: ICarRepository) {
    operator fun invoke(car: Car, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) =
            repo.addCar(car, onSuccess, onFailure)
    fun addCarWithEvents(
        car: Car,
        events: List<Event>,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) = repo.addCarWithEvents(car, events, onSuccess, onFailure)
}
