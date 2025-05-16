package com.example.drivocare.usecase

import com.example.drivocare.data.Event
import com.example.drivocare.repositories.ICarRepository

class AddEventUseCase(private val repo: ICarRepository) {
    operator fun invoke(event: Event, carId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) =
        repo.addEvent(event, carId, onSuccess, onFailure)
}