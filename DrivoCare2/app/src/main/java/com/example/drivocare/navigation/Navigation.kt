package com.example.drivocare.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.drivocare.ui.screens.*
import com.example.drivocare.viewmodel.*

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
    val noBottomNavRoutes = listOf("login", "register", "forgot_password")
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        topBar = { if (currentRoute in showTopBarRoutes) TopNavBar(navController) },
        bottomBar = { if (currentRoute !in noBottomNavRoutes) BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = modifier.padding(innerPadding)
        ) {
            composable("login") { LoginPage(modifier, navController, authViewModel) }
            composable("register") { RegisterPage(modifier, navController, authViewModel) }
            composable("home") { HomePage(modifier, navController, authViewModel, postViewModel) }
            composable("myposts") { MyPostsPage(modifier, navController, authViewModel, postViewModel) }
            composable("settings") { SettingsPage(modifier, navController, authViewModel, addCarViewModel, postViewModel, myCarsViewModel) }
            composable("inbox") { InboxPage(modifier, navController, authViewModel, notificationViewModel, myCarsViewModel) }
            composable("newpost") { NewPostPage(modifier, navController, authViewModel, postViewModel) }
            composable("scanning") { ScanningPage(modifier, navController, authViewModel) }
            composable("addcar") { AddCarPage(modifier, navController, authViewModel, addCarViewModel) }
            composable("mycars") { MyCarsPage(modifier, navController, authViewModel, addCarViewModel, myCarsViewModel) }
            composable("forgot_password") { ForgotPasswordPage(modifier, navController, authViewModel) }
            composable("addevent/{carId}/{date}") { backStackEntry ->
                val carId = backStackEntry.arguments?.getString("carId")
                val selectedDateArg = backStackEntry.arguments?.getString("date")
                AddEventPage(carId = carId, selectedDateArg = selectedDateArg, navController = navController, eventViewModel = addEventViewModel, carViewModel = addCarViewModel)
            }


            composable("addevent") {
                AddEventPage(null, selectedDateArg = null, navController, addEventViewModel, addCarViewModel)
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
