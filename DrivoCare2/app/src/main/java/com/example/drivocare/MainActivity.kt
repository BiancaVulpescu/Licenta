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
import com.example.drivocare.navigation.Navigation
import com.example.drivocare.repositories.AuthRepository
import com.example.drivocare.repositories.PostRepository
import com.example.drivocare.ui.theme.DrivoCareTheme
import com.example.drivocare.usecase.AddPostUseCase
import com.example.drivocare.usecase.LoginUseCase
import com.example.drivocare.usecase.RegisterUseCase
import com.example.drivocare.viewmodel.AddCarViewModel
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.PostDetailViewModel
import com.example.drivocare.viewmodel.PostViewModel

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

        val addCarViewModel: AddCarViewModel by viewModels()
        setContent{
            DrivoCareTheme{
                Scaffold(modifier= Modifier.fillMaxSize()){ innerPadding->
                    Navigation(modifier=Modifier.padding(innerPadding), authViewModel=authViewModel, addCarViewModel=addCarViewModel, postViewModel=postViewModel, postDetailViewModel= postDetailViewModel)
                }
            }
        }
    }
}