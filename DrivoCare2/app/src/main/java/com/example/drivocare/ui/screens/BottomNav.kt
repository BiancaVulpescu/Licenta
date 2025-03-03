package com.example.drivocare.ui.screens

import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.drivocare.R

@Composable
fun BottomNavBar(navController: NavController) {
    val items= listOf(
        BottomNavItem.Home,
        BottomNavItem.Scanning,
        BottomNavItem.MyCars,
        BottomNavItem.Settings
    )
    NavigationBar(containerColor= Color(0xFF479195)){
        val
    }
}d
sealed class BottomNavItem(val route: String, val icon: Int)
{
    object Home : BottomNavItem("home", R.drawable.home)
    object Scanning : BottomNavItem("scanning", R.drawable.scan)
    object MyCars : BottomNavItem("mycars", R.drawable.car)
    object Settings : BottomNavItem("settings", R.drawable.setting)
}