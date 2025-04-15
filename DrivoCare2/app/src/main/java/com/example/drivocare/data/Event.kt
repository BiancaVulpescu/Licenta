package com.example.drivocare.data

import com.google.firebase.Timestamp

data class Event (
        val Description: String = "",
        val NotificationSet: Boolean = false,
        val Title: String = "",
        val StartDate: Timestamp ,
        val EndDate: Timestamp
    )