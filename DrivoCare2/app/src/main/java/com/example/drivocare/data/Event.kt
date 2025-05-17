package com.example.drivocare.data

import com.google.firebase.Timestamp

data class Event(
        val id: String = "",
        val carId: String = "",
        val userId: String = "",
        val title: String = "",
        val description: String="",
        val startDate: Timestamp = Timestamp.now(),
        val endDate: Timestamp = Timestamp.now(),
        val notificationSet: Boolean= false
)
