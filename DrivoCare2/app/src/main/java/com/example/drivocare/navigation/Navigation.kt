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
import com.example.drivocare.viewmodel.AddEventViewModel
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.MyCarsViewModel
import com.example.drivocare.viewmodel.NotificationViewModel
import com.example.drivocare.viewmodel.PostDetailViewModel
import com.example.drivocare.viewmodel.PostViewModel
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    addCarViewModel: AddCarViewModel,
    myCarsViewModel: MyCarsViewModel,
    addEventViewModel: AddEventViewModel,
    postViewModel: PostViewModel,
    postDetailViewModel: PostDetailViewModel,
    notificationViewModel: NotificationViewModel
) {
    val navController = rememberNavController()
    val showTopBarRoutes = listOf("home", "myposts", "inbox", "newpost")
    val noBottomNavRoutes = listOf("login", "register")
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        topBar = { if (currentRoute in showTopBarRoutes) TopNavBar(navController) },
        bottomBar = { if (currentRoute !in noBottomNavRoutes) BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(navController, startDestination = "login", modifier = Modifier.padding(innerPadding)) {
            composable("login") { LoginPage(modifier, navController, authViewModel) }
            composable("register") { RegisterPage(modifier, navController, authViewModel) }
            composable("home") { HomePage(modifier, navController, authViewModel, postViewModel) }
            composable("myposts") { MyPostsPage(modifier, navController, authViewModel, postViewModel) }
            composable("settings") { SettingsPage(modifier, navController, authViewModel, addCarViewModel, postViewModel, myCarsViewModel) }
            composable("inbox") { InboxPage(modifier, navController, authViewModel, notificationViewModel) }
            composable("newpost") { NewPostPage(modifier, navController, authViewModel, postViewModel) }
            composable("scanning") { ScanningPage(modifier, navController, authViewModel) }
            composable("addcar") { AddCarPage(modifier, navController, authViewModel, addCarViewModel) }
            composable("mycars") { MyCarsPage(modifier, navController, authViewModel, addCarViewModel, myCarsViewModel) }
            composable("addevent/{carId}") {
                val carId = it.arguments?.getString("carId")
                AddEventPage(carId, navController, addEventViewModel, addCarViewModel)
            }
            composable("addevent") {
                AddEventPage(null, navController, addEventViewModel, addCarViewModel)
            }
            composable("postDetail/{postId}") {
                val postId = it.arguments?.getString("postId") ?: ""
                PostDetailPage(modifier, postId, navController, authViewModel, postDetailViewModel)
            }
            composable("warning/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                WarningLightPage(id = id)
            }
        }
    }
}
