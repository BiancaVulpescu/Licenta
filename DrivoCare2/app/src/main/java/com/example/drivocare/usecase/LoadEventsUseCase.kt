package com.example.drivocare.usecase

import com.example.drivocare.data.Event
import com.example.drivocare.repositories.ICarRepository
import kotlinx.coroutines.flow.Flow

class LoadEventsUseCase(private val repo: ICarRepository) {
    operator fun invoke(carId: String): Flow<List<Event>> = repo.getEvents(carId)
}