package com.example.drivocare.data
import java.util.Date

sealed class NotificationItem(val id: String) {
    data class CommentNotification(
        val postId: String,
        val message: String,
        val time: Date
    ) :
        NotificationItem(postId + time.time)

    data class CarEventNotification(
        val eventId: String,
        val title: String,
        val endDate: Date) :
        NotificationItem(eventId)
}
