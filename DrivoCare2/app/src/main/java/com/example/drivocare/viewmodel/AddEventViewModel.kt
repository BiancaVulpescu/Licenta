package com.example.drivocare.viewmodel

import android.util.Log
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
    var startTime = mutableStateOf("")
    var endTime= mutableStateOf("")

    fun buildValidatedEvent(): Result<Event> {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val startDateTimeStr="${startDate.value.trim()} ${startTime.value.trim().ifBlank { "00:00" }}"
        val endDateTimeStr="${endDate.value.trim()} ${endTime.value.trim().ifBlank { "00:00" }}"
        Log.d("TimestampDebug", "Parsing start: '$startDateTimeStr'")
        val start = try {
            Timestamp(dateFormat.parse(startDateTimeStr)!!)
        } catch (e: Exception) {
            return Result.failure(Exception("Start date/time must be in dd-MM-yyyy/HH:mm format"))
        }

        val end = try {
            Timestamp(dateFormat.parse(endDateTimeStr)!!)
        } catch (e: Exception) {
            return Result.failure(Exception("End date/time must be in dd-MM-yyyy/HH:mm format"))
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
