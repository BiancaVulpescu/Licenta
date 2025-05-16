package com.example.drivocare.usecase

import com.example.drivocare.data.Car
import com.example.drivocare.repositories.ICarRepository
import kotlinx.coroutines.flow.Flow

class LoadCarsUseCase(private val repo: ICarRepository) {
    operator fun invoke(userId: String): Flow<List<Car>> = repo.getCars(userId)
}