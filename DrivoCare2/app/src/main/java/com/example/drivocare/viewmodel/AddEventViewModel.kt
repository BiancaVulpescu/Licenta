package com.example.drivocare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivocare.data.Event
import com.example.drivocare.usecase.AddEventUseCase
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.*

class AddEventViewModel(private val addEventUseCase: AddEventUseCase) : ViewModel() {

    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    val startDate = MutableStateFlow("")
    val startTime = MutableStateFlow("")
    val endDate = MutableStateFlow("")
    val endTime = MutableStateFlow("")
    val notificationSet = MutableStateFlow(false)

    fun reset() {
        title.value = ""
        description.value = ""
        startDate.value = ""
        startTime.value = ""
        endDate.value = ""
        endTime.value = ""
        notificationSet.value = false
    }

    fun buildValidatedEvent(userId: String, carId: String): Result<Event> {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val startDateTimeStr = "${startDate.value.trim()} ${startTime.value.trim().ifBlank { "00:00" }}"

        val start = try {
            Timestamp(dateFormat.parse(startDateTimeStr)!!)
        } catch (e: Exception) {
            return Result.failure(Exception("Start date/time must be in dd-MM-yyyy/HH:mm format"))
        }

        val end = if (endDate.value.isNotBlank()) {
            val endDateTimeStr = "${endDate.value.trim()} ${endTime.value.trim().ifBlank { "00:00" }}"
            try {
                Timestamp(dateFormat.parse(endDateTimeStr)!!)
            } catch (e: Exception) {
                return Result.failure(Exception("End date/time must be in dd-MM-yyyy/HH:mm format"))
            }
        } else {
            start
        }

        if (title.value.isBlank()) {
            return Result.failure(Exception("Title is required"))
        }

        return Result.success(
            Event(
                id = "",
                carId = carId,
                userId = userId,
                title = title.value,
                description = description.value,
                startDate = start,
                endDate = end,
                notificationSet = notificationSet.value
            )
        )
    }

    fun saveEvent(event: Event, onSuccess: () -> Unit, onError: (String) -> Unit) {
        addEventUseCase(event, event.carId, onSuccess, onError)
    }

}
