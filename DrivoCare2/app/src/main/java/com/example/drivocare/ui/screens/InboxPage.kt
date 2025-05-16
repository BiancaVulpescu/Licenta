package com.example.drivocare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.NotificationViewModel
import com.example.drivocare.data.NotificationItem
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InboxPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: NotificationViewModel
) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Notifications", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("You're all caught up!", fontSize = 16.sp)
            }
        } else {
            LazyColumn {
                items(notifications, key = { it.id }) { notif ->
                    NotificationCard(notif)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
    val dateFormat = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            when (item) {
                is NotificationItem.CommentNotification -> {
                    Text(item.message, fontWeight = FontWeight.Bold)
                    Text(dateFormat.format(item.time), fontSize = 12.sp, color = Color.Gray)
                }

                is NotificationItem.CarEventNotification -> {
                    Text("Upcoming: ${item.name}", fontWeight = FontWeight.Bold)
                    Text("Ends: ${dateFormat.format(item.endDate)}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
