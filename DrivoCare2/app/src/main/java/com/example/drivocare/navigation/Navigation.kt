package com.example.drivocare.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.drivocare.ui.screens.*
import com.example.drivocare.viewmodel.AuthViewModel

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val showTopBarRoutes = listOf("home", "myposts", "inbox", "newpost")
    val noBottomNavRoutes = listOf("login", "register")
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute in showTopBarRoutes) {
                TopNavBar(navController)
            }
        },
        bottomBar = {
            if (currentRoute !in noBottomNavRoutes) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginPage(modifier, navController, authViewModel) }
            composable("register") { RegisterPage(modifier, navController, authViewModel) }
            composable("home") { HomePage(modifier, navController, authViewModel) }
            composable("scanning") { ScanningPage(modifier, navController, authViewModel) }
            composable("mycars") { MyCarsPage(modifier, navController,authViewModel) }
            composable("settings") { SettingsPage(modifier, navController,authViewModel) }
            composable("myposts") { MyPostsPage(modifier, navController, authViewModel) }
            composable("inbox") { InboxPage(modifier, navController, authViewModel) }
            composable("newpost") { NewPostPage(modifier, navController, authViewModel) }
            composable("warning/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                WarningLightPage(id = id)
            }

        }
    }
}
