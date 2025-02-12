package com.example.drivocare

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.drivocare.pages.HomePage
import com.example.drivocare.pages.LoginPage
import com.example.drivocare.pages.RegisterPage

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