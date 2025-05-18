package com.example.drivocare.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.drivocare.viewmodel.MyCarsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InboxPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: NotificationViewModel,
    myCarsViewModel: MyCarsViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFCBD2D6))
            .padding(top=16.dp)
    ) {
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your inbox is empty", fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    NotificationCard(notif, navController, myCarsViewModel)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem, navController: NavController, viewModel: MyCarsViewModel) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable {
                when (item) {
                    is NotificationItem.CommentNotification -> {
                        navController.navigate("postDetail/${item.postId}")
                    }

                    is NotificationItem.CarEventNotification -> {
                        val index = viewModel.cars.value.indexOfFirst { it.id == item.carId }
                        if (index >= 0) {
                            viewModel.selectedCarIndex.value = index
                        }
                        navController.navigate("mycars")
                    }
                }
            },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            when (item) {
                is NotificationItem.CommentNotification -> {
                    Text(item.message, fontWeight = FontWeight.Bold)
                    Text(dateFormat.format(item.time), fontSize = 12.sp, color = Color.Gray)
                }

                is NotificationItem.CarEventNotification -> {
                    Text("Upcoming: ${item.title}", fontWeight = FontWeight.Bold)
                    Text("Ends: ${dateFormat.format(item.endDate)}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
