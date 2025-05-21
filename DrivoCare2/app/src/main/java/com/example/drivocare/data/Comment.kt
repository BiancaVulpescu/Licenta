package com.example.drivocare.data

import com.google.firebase.Timestamp

data class Comment(
    val userId: String = "",
    val username: String = "",
    val text: String = "",
    val time: Timestamp = Timestamp.now(),
    val postId: String = "",
    val imageUrl: String? = null
)
