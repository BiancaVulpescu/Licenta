package com.example.drivocare.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.Event
import com.example.drivocare.repositories.EventRepository
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class AddEventViewModel : ViewModel() {
    private val repository = EventRepository()

    var description = mutableStateOf("")
    var notificationSet = mutableStateOf(false)
    var title = mutableStateOf("")
    var startDate = mutableStateOf("")
    var endDate = mutableStateOf("")

    fun buildValidatedEvent(): Result<Event> {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val start = try {
            Timestamp(formatter.parse(startDate.value)!!)
        } catch (e: Exception) {
            return Result.failure(Exception("Start date must be in dd-MM-yyyy format"))
        }

        val end = try {
            Timestamp(formatter.parse(endDate.value)!!)
        } catch (e: Exception) {
            return Result.failure(Exception("End date must be in dd-MM-yyyy format"))
        }

        if (title.value.isBlank()) {
            return Result.failure(Exception("Title is required"))
        }

        return Result.success(
            Event(
                description = description.value,
                notificationSet = notificationSet.value,
                title = title.value,
                startDate = start,
                endDate = end
            )
        )
    }
    fun saveEvent(carId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val result = buildValidatedEvent()
        result.fold(
            onSuccess = { event ->
                repository.addEvent(carId, event, onSuccess, onError)
            },
            onFailure = { ex ->
                onError(ex.message ?: "Invalid event")
            }
        )
    }
}
