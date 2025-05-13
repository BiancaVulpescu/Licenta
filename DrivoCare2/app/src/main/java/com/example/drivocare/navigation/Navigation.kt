package com.example.drivocare.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.drivocare.ui.screens.*
import com.example.drivocare.viewmodel.AddCarViewModel
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.PostDetailViewModel
import com.example.drivocare.viewmodel.PostViewModel

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, addCarViewModel: AddCarViewModel) {
    val navController = rememberNavController()
    val showTopBarRoutes = listOf("home", "myposts", "inbox", "newpost")
    val noBottomNavRoutes = listOf("login", "register")
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val postViewModel: PostViewModel = viewModel()
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
            composable("home") { HomePage(modifier, navController, authViewModel, postViewModel) }
            composable("scanning") { ScanningPage(modifier, navController, authViewModel) }
            composable("mycars") { MyCarsPage(modifier, navController,authViewModel, addCarViewModel) }
            composable("settings") { SettingsPage(modifier, navController,authViewModel,addCarViewModel,postViewModel) }
            composable("myposts") { MyPostsPage(modifier, navController, authViewModel, postViewModel) }
            composable("inbox") { InboxPage(modifier, navController, authViewModel) }
            composable("newpost") { NewPostPage(modifier, navController, authViewModel, postViewModel) }
            composable("warning/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                WarningLightPage(id = id)
            }
            composable("addcar") { AddCarPage(modifier, navController, authViewModel, addCarViewModel) }
            composable("addevent/{carId}") { backStackEntry ->
                val carId = backStackEntry.arguments?.getString("carId")
                AddEventPage(
                    navController = navController,
                    carId = carId.takeIf { it?.isNotBlank() == true }, carViewModel = addCarViewModel
                )
            }
            composable("addevent") { AddEventPage(navController = navController, carId=null, carViewModel = addCarViewModel) }
            composable("postDetail/{postId}", arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                val postDetailViewModel: PostDetailViewModel = viewModel()
                PostDetailPage(modifier = modifier, postId = postId, navController = navController, authViewModel = authViewModel, viewModel = postDetailViewModel)
            }
        }
    }
}
