package com.example.drivocare.ui.screens

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
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
        val navBackStackEntry= navController.currentBackStackEntryAsState()
        val currentRoute= navBackStackEntry.value?.destination?.route
        items.forEach{ item->
            NavigationBarItem(
                icon={
                    Icon(
                        painter= painterResource(id=item.icon),
                        contentDescription=item.label,
                        tint = if (currentRoute == item.route) Color.Red else Color.White
                    )
                },
                selected=currentRoute==item.route,
                onClick={navController.navigate(item.route)},
                alwaysShowLabel = false
            )
        }
    }
}
sealed class BottomNavItem(val route: String, val icon: Int, val label:String)
{
    object Home : BottomNavItem("home", R.drawable.home, "Home")
    object Scanning : BottomNavItem("scanning", R.drawable.scan, "Scanning")
    object MyCars : BottomNavItem("mycars", R.drawable.car, "MyCars")
    object Settings : BottomNavItem("settings", R.drawable.setting,"Settings")
}