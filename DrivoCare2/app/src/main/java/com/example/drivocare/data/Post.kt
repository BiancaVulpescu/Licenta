package com.example.drivocare.data

import com.google.firebase.Timestamp

data class Post(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val contentText: String? = null,
    val imageUrl: String? = null,
    val time: Timestamp = Timestamp.now()
)
