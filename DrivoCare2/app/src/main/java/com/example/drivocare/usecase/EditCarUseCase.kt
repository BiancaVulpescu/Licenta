package com.example.drivocare.usecase

import com.example.drivocare.data.Car
import com.example.drivocare.repositories.ICarRepository

class EditCarUseCase(private val repo: ICarRepository) {
    operator fun invoke(carId: String, car: Car, onSuccess: () -> Unit, onFailure: (String) -> Unit) =
        repo.updateCar(carId, car, onSuccess, onFailure)
}