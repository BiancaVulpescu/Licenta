package com.example.drivocare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.drivocare.navigation.Navigation
import com.example.drivocare.repositories.AuthRepository
import com.example.drivocare.repositories.CarRepository
import com.example.drivocare.repositories.PostRepository
import com.example.drivocare.ui.theme.DrivoCareTheme
import com.example.drivocare.usecase.*
import com.example.drivocare.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = AuthRepository()
        val loginUseCase = LoginUseCase(authRepository)
        val registerUseCase = RegisterUseCase(authRepository)
        val authViewModel = AuthViewModel(loginUseCase, registerUseCase, authRepository)

        val postRepository = PostRepository()
        val addPostUseCase = AddPostUseCase(postRepository)
        val postViewModel = PostViewModel(addPostUseCase, postRepository)
        val postDetailViewModel = PostDetailViewModel(postRepository)

        val carRepository = CarRepository()
        val addCarUseCase = AddCarUseCase(carRepository)
        val editCarUseCase = EditCarUseCase(carRepository)
        val loadCarsUseCase = LoadCarsUseCase(carRepository)
        val loadEventsUseCase = LoadEventsUseCase(carRepository)
        val addEventUseCase = AddEventUseCase(carRepository)

        val addCarViewModel = AddCarViewModel(addCarUseCase, editCarUseCase, addEventUseCase)
        val myCarsViewModel = MyCarsViewModel(loadCarsUseCase, loadEventsUseCase)
        val addEventViewModel = AddEventViewModel(addEventUseCase)
        val notificationViewModel = NotificationViewModel(application, postRepository, carRepository)

        setContent {
            DrivoCareTheme {
                    Navigation(
                        modifier = Modifier,
                        authViewModel = authViewModel,
                        addCarViewModel = addCarViewModel,
                        myCarsViewModel = myCarsViewModel,
                        postViewModel = postViewModel,
                        postDetailViewModel = postDetailViewModel,
                        addEventViewModel = addEventViewModel,
                        notificationViewModel= notificationViewModel
                    )
            }
        }
    }
}
