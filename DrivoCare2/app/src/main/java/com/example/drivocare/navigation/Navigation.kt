package com.example.drivocare.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.drivocare.ui.screens.HomePage
import com.example.drivocare.ui.screens.LoginPage
import com.example.drivocare.ui.screens.RegisterPage
import com.example.drivocare.viewmodel.AuthViewModel

@Composable
fun Navigation(modifier: Modifier= Modifier, authViewModel: AuthViewModel) {
    val navController= rememberNavController()
    NavHost(navController=navController, startDestination = "login", builder={
        composable("login"){
            LoginPage(modifier, navController,authViewModel)
        }
        composable("register"){
            RegisterPage(modifier, navController,authViewModel)
        }
        composable("home"){
            HomePage(modifier, navController,authViewModel)
        }
    })
}