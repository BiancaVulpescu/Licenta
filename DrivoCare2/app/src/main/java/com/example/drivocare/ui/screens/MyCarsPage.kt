package com.example.drivocare.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MyCarsPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: MyCarsViewModel = viewModel()
) {
    val authState = authViewModel.authState.observeAsState()
    val cars = viewModel.cars.observeAsState(listOf())
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedCarIndex by remember { mutableStateOf(0) }

    val arrowRotationDegree by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

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
        Button(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C141E)),
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            contentPadding = PaddingValues(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.car),
                        contentDescription = "Car icon",
                        tint = Color.Black,
                        modifier = Modifier.size(45.dp)
                    )
                    Spacer(modifier = Modifier.width(25.dp))
                    Column {
                        Text(
                            text = if (cars.value.isNotEmpty()) "${cars.value[selectedCarIndex].brand} ${cars.value[selectedCarIndex].model}" else "No car selected",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (cars.value.isNotEmpty()) {
                            Text(
                                text = cars.value[selectedCarIndex].number,
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(35.dp)
                        .rotate(arrowRotationDegree)
                )
            }
        }

        if (expanded) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                cars.value.forEachIndexed { index, car ->
                    if (index != selectedCarIndex) {
                        Button(
                            onClick = {
                                selectedCarIndex = index
                                expanded = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C141E)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape,
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(20.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.car),
                                    contentDescription = "Car icon",
                                    tint = Color.Black,
                                    modifier = Modifier.size(45.dp)
                                )
                                Spacer(modifier = Modifier.width(25.dp))
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
                        }
                    }
                }

                Button(
                    onClick = {
                        expanded = false
                        navController.navigate("addcar")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C141E)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "Add car",
                            tint = Color.White,
                            modifier = Modifier.size(35.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Add car",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (cars.value.isNotEmpty()) {
            Button(
                onClick = {
                    val selectedCarId = cars.value[selectedCarIndex].id
                    navController.navigate("addevent/$selectedCarId")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C141E)),
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Text("Add Event", color = Color.White)
            }
        }
    }
}



