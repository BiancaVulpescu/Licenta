package com.example.drivocare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivocare.data.Car
import com.example.drivocare.data.Event
import com.example.drivocare.usecase.AddCarUseCase
import com.example.drivocare.usecase.AddEventUseCase
import com.example.drivocare.usecase.EditCarUseCase
import kotlinx.coroutines.flow.MutableStateFlow

class AddCarViewModel(
    private val addCarUseCase: AddCarUseCase,
    private val editCarUseCase: EditCarUseCase,
    private val addEventUseCase: AddEventUseCase
) : ViewModel() {

    val brand = MutableStateFlow("")
    val model = MutableStateFlow("")
    val year = MutableStateFlow("")
    val number = MutableStateFlow("")
    val isEditMode = MutableStateFlow(false)
    var editingCarId: String? = null

    val pendingEvents = mutableListOf<Event>()

    fun addPendingEvent(event: Event) {
        pendingEvents.add(event)
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

    fun saveCar(userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val car = Car(
            id = editingCarId ?: "",
            userId = userId,
            brand = brand.value,
            model = model.value,
            year = year.value.toIntOrNull() ?: 0,
            number = number.value
        )

        if (isEditMode.value && editingCarId != null) {
            editCarUseCase(editingCarId!!, car, {
                onSuccess()
            }, onError)
        } else {
            if (pendingEvents.isNotEmpty()) {
                addCarUseCase.addCarWithEvents(car, pendingEvents, { carId ->
                    pendingEvents.clear()
                    onSuccess()
                }, onError)
            } else {
                addCarUseCase(car, {
                    onSuccess()
                }, onError)
            }
        }
    }
}
