package com.example.drivocare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drivocare.R
import com.example.drivocare.data.AuthState
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.MyCarsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun MyCarsPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, viewModel: MyCarsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val authState = authViewModel.authState.observeAsState()
    val cars = viewModel.cars.observeAsState(listOf())
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedCarIndex by remember { mutableStateOf(0) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            is AuthState.Authenticated -> viewModel.loadCarsForCurrentUser()
            else -> Unit
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box {
            Button(
                onClick = { expanded = !expanded },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C141E)),
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.car),
                        contentDescription = "Car icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (cars.value.isNotEmpty()) "${cars.value[selectedCarIndex].brand} ${cars.value[selectedCarIndex].model}" else "No car selected",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (cars.value.isNotEmpty()) {
                            Text(
                                text = cars.value[selectedCarIndex].number,
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "Dropdown arrow",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                cars.value.forEachIndexed { index, car ->
                    if (index != selectedCarIndex) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.car),
                                        contentDescription = "Car icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("${car.brand} ${car.model}", color = Color.White)
                                        Text(car.number, fontSize = 12.sp, color = Color.LightGray)
                                    }
                                }
                            },
                            onClick = {
                                selectedCarIndex = index
                                expanded = false
                            },
                            modifier = Modifier.background(Color(0xFF9C141E))
                        )
                    }
                }

                Divider(color = Color.LightGray)

                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.add),
                                contentDescription = "Add car",
                                tint = Color(0xFF479195),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add car", color = Color(0xFF479195))
                        }
                    },
                    onClick = {
                        expanded = false
                        navController.navigate("addcar")
                    }
                )
            }
        }
    }
}



