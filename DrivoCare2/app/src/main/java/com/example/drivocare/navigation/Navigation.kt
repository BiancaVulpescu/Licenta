package com.example.drivocare.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.drivocare.viewmodels.AuthViewModel
import com.example.drivocare.pages.HomePage
import com.example.drivocare.pages.LoginPage
import com.example.drivocare.pages.RegisterPage
@Composable
fun Navigation(authController: AuthController) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginPage(navController = navController, authController = authController) }
        composable("register") { RegisterPage(navController = navController, authController = authController) }
        composable("home") { HomePage(navController = navController, authController = authController) }
    }
}
