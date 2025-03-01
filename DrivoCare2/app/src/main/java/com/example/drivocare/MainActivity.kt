package com.example.drivocare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.drivocare.controllers.AuthController
import com.example.drivocare.data.repositories.AuthRepository
import com.example.drivocare.navigation.Navigation
import com.example.drivocare.viewmodels.AuthViewModel
import com.example.drivocare.ui.theme.DrivoCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = AuthRepository()
        val authController = AuthController(authRepository)

        setContent {
            DrivoCareTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(modifier = Modifier.padding(innerPadding), authController = authController)
                }
            }
        }d
    }
}