package com.example.drivocare.ui.screens

import android.R.id.bold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drivocare.R
import com.example.drivocare.data.AuthState
import com.example.drivocare.viewmodel.AddCarViewModel
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.MyCarsViewModel
import com.example.drivocare.viewmodel.PostViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, addCarViewModel: AddCarViewModel, postViewModel: PostViewModel, myCarsViewModel: MyCarsViewModel ) {
    val authState by authViewModel.authState.collectAsState()
    val cars by myCarsViewModel.cars.collectAsState()
    val selectedCarIndex by myCarsViewModel.selectedCarIndex.collectAsState()

    val username by authViewModel.currentUsername.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var showChangeUsernameDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordChangeError by remember { mutableStateOf<String?>(null) }

    var colorBlue =Color(0xFF479195)
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated && userId != null) {
            myCarsViewModel.loadCarsForUser(userId)
        } else if (authState is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    LaunchedEffect(selectedCarIndex, cars) {
        val currentCar = cars.getOrNull(selectedCarIndex)
        if (currentCar != null) {
            myCarsViewModel.loadEventsForCar(currentCar.id)
        }
    }
    val arrowRotationDegree by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFCBD2D6))
        .let {
            if (showChangeUsernameDialog || showChangePasswordDialog) it.blur(8.dp) else it
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Button(
                onClick = { expanded = !expanded },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(
                                text = "My cars",
                                color = colorBlue,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = colorBlue,
                        modifier = Modifier
                            .size(32.dp)
                            .rotate(arrowRotationDegree)
                    )
                }
            }

            if (expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cars.forEachIndexed { index, car ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorBlue)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.car),
                                    contentDescription = "Car Icon",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "${car.brand} ${car.model}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = car.number,
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    addCarViewModel.brand.value = car.brand
                                    addCarViewModel.model.value = car.model
                                    addCarViewModel.year.value = car.year.toString()
                                    addCarViewModel.number.value = car.number
                                    addCarViewModel.isEditMode.value = true
                                    addCarViewModel.editingCarId = car.id

                                    expanded = false
                                    navController.navigate("addcar")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RectangleShape,
                            ) {
                                Text("Edit", color = colorBlue)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            addCarViewModel.reset()
                            expanded = false
                            navController.navigate("addcar")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorBlue),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.add),
                                contentDescription = "Add Car",
                                tint = Color.White,
                                modifier = Modifier.size(35.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Add car", color = Color.White, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
            TextButton(
                onClick = { authViewModel.logout()
                            postViewModel.clearPost()
                          },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 12.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Logout",
                        color = colorBlue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            TextButton(
                onClick = {  authViewModel.fetchCurrentUsername()
                    newUsername = ""
                    showChangeUsernameDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 12.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Change username",
                        color = colorBlue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            TextButton(
                onClick = {currentPassword = ""
                    newPassword = ""
                    passwordChangeError = null
                    showChangePasswordDialog = true  },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 12.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Change password",
                        color = colorBlue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

        }
        if (showChangeUsernameDialog) {
            AlertDialog(
                onDismissRequest = { showChangeUsernameDialog = false },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Change Username",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorBlue
                        )
                    }
                },
                text = {
                    Column {
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        Text(
                            text = "Current: $username",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        OutlinedTextField(
                            value = newUsername,
                            onValueChange = { newUsername = it },
                            label = { Text("New Username", color= colorBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { showChangeUsernameDialog = false }) {
                                Text("Cancel", color = colorBlue, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            }
                            TextButton(onClick = {
                                authViewModel.updateUsername(
                                    newUsername,
                                    onSuccess = {
                                        authViewModel.fetchCurrentUsername()
                                        showChangeUsernameDialog = false },
                                    onError = { }
                                )
                            }) {
                                Text("Save", color = colorBlue, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }

        if (showChangePasswordDialog) {
            AlertDialog(
                onDismissRequest = { showChangePasswordDialog = false },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Change Password",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorBlue
                        )
                    }
                },
                text = {
                    Column {
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Current Password", color = colorBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password", color = colorBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black
                            )
                        )
                        if (!passwordChangeError.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = passwordChangeError ?: "",
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { showChangePasswordDialog = false }) {
                                Text(
                                    "Cancel",
                                    color = colorBlue,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            TextButton(onClick = {
                                authViewModel.changePassword(
                                    currentPassword,
                                    newPassword,
                                    onSuccess = { showChangePasswordDialog = false },
                                    onError = { passwordChangeError = it }
                                )
                            }) {
                                Text(
                                    "Save",
                                    color = colorBlue,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }


    }
}




