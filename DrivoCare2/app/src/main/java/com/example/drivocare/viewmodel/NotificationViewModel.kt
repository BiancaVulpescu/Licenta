package com.example.drivocare.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivocare.data.Comment
import com.example.drivocare.data.Post
import com.example.drivocare.data.Event
import com.example.drivocare.repositories.IPostRepository
import com.example.drivocare.repositories.ICarRepository
import com.example.drivocare.data.NotificationItem
import com.example.drivocare.data.NotificationItem.CarEventNotification
import com.example.drivocare.data.NotificationItem.CommentNotification
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NotificationViewModel(
    app: Application,
    private val postRepository: IPostRepository,
    private val carRepository: ICarRepository
) : AndroidViewModel(app) {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications

    private val shownEventPrefs =
        app.getSharedPreferences("shown_car_event_notifications", Context.MODE_PRIVATE)

    private var hasLoaded = false

    fun loadNotifications() {
        if (hasLoaded) return
        hasLoaded = true

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            combine<List<Post>, List<Comment>, List<Event>, List<NotificationItem>>(
                postRepository.getPosts(),
                postRepository.getAllComments(),
                carRepository.getFutureEventsForUser(userId)
            ) { posts, comments, events ->

                val myPostIds = posts.filter { it.userId == userId }.map { it.id }

                val commentNotifs = comments.filter { it.postId in myPostIds }.map {
                    CommentNotification(
                        postId = it.postId,
                        message = "${it.username} commented on your post: ${it.text.take(50)}",
                        time = it.time.toDate()
                    )
                }

                val upcomingEvents = events.filter {
                     it.notificationSet &&
                            it.endDate.toDate().before(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) &&
                     it.endDate.toDate() != it.startDate.toDate()
                }

                val carEventNotifs = upcomingEvents.map {
                    markEventAsShownToday(it.id)
                    CarEventNotification(
                        eventId = it.id,
                        title = it.title,
                        endDate = it.endDate.toDate(),
                        carId = it.carId
                    )
                }

                (commentNotifs + carEventNotifs).sortedByDescending {
                    when (it) {
                        is CommentNotification -> it.time
                        is CarEventNotification -> it.endDate
                    }
                }
            }.distinctUntilChanged()
                .onEach {
                    if (it.isNotEmpty()) {
                        _notifications.value = it
                    }
                }.launchIn(viewModelScope)
        }
    }

    private fun wasEventShownToday(eventId: String): Boolean {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val key = "${eventId}_$today"
        return shownEventPrefs.getBoolean(key, false)
    }

    private fun markEventAsShownToday(eventId: String) {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val key = "${eventId}_$today"
        shownEventPrefs.edit().putBoolean(key, true).apply()
    }
}
