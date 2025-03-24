package com.example.drivocare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.drivocare.R

@Composable
fun TopNavBar(navController: NavController) {
    val items = listOf(
        TopNavItem.NewPost,
        TopNavItem.MyPosts,
        TopNavItem.Inbox,
    )
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(top = statusBarHeight)
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5)),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach { item ->
            Box(
                modifier = Modifier
                    .weight(1f) //distribuie cele 3 spatii pt butoane in mod egal
                    .clickable { navController.navigate(item.route) }
                    .background(Color(0xFF479195))
                    .padding(vertical=8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) Color(0xFF9C141E) else Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp)) //dintre icon si text
                    Text(
                        text = item.label,
                        color = if (currentRoute == item.route) Color(0xFF9C141E) else Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    }
}
sealed class TopNavItem(val route: String, val icon: Int, val label:String)
{
    object NewPost: TopNavItem("newpost", R.drawable.add, "New post")
    object MyPosts : TopNavItem("myposts", R.drawable.eye, "My Posts")
    object Inbox : TopNavItem("inbox", R.drawable.notification, "Inbox")
}