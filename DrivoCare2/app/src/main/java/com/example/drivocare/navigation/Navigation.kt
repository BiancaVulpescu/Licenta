package com.example.drivocare.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.drivocare.data.AuthState
import com.example.drivocare.ui.screens.*
import com.example.drivocare.viewmodel.AuthViewModel

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    val authState by authViewModel.authState.collectAsState()
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> navController.navigate("login") {
                popUpTo("home") { inclusive = true } // Prevent back navigation
            }
            is AuthState.Authenticated -> {
                if (navController.currentBackStackEntry?.destination?.route == "login") {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true } // Clear login from backstack
                    }
                }
            }
            else -> Unit // No action for loading or errors
        }
    }

    val noBottomNavRoutes = listOf("login", "register")
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
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
        }
    }
}
