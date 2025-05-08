package com.example.drivocare.data

import com.google.firebase.Timestamp

data class Event (
        val description: String = "",
        val notificationSet: Boolean = false,
        val title: String=" " ,
        val startDate: Timestamp = Timestamp.now(),
        val endDate: Timestamp = Timestamp.now(),
        val id: String = ""
    )