package com.example.drivocare.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drivocare.viewmodel.AddCarViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.YearMonth
import java.time.ZoneId
import java.time.LocalDate
import com.example.drivocare.viewmodel.MyCarsViewModel
import com.example.drivocare.data.AuthState
import com.example.drivocare.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCarsPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    addCarViewModel: AddCarViewModel,
    viewModel: MyCarsViewModel
) {
    val authState by authViewModel.authState.collectAsState()
    val cars by viewModel.cars.collectAsState()
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val selectedCarIndex by viewModel.selectedCarIndex.collectAsState()
    val events by viewModel.events.collectAsState()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val warningLights = listOf(
        "abs", "air_suspension", "airbag_indicator", "battery", "brake",
        "catalytic_converter", "check_engine", "engine_temperature",
        "fuel_filter", "glow_plug", "high_beam_light", "hood_open",
        "low_fuel", "master_warning", "oil_pressure", "parking_brake",
        "powertrain", "seat_belt", "tire_pressure", "traction_control",
        "traction_control_off", "transmission_temperature"
    )
    val arrowRotationDegree by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (!userId.isNullOrBlank()) {
                viewModel.loadCarsForUser(userId)
            }
        } else if (authState is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }


    LaunchedEffect(selectedCarIndex, cars) {
        val currentCar = cars.getOrNull(selectedCarIndex)
        if (currentCar != null) {
            viewModel.loadEventsForCar(currentCar.id)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFCBD2D6))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(20.dp))
                        Icon(
                            painter = painterResource(id = com.example.drivocare.R.drawable.car),
                            contentDescription = "Car icon",
                            tint = Color.Black,
                            modifier = Modifier.size(45.dp)
                        )
                        Spacer(modifier = Modifier.width(25.dp))
                        Column {
                            val currentCar = cars.getOrNull(selectedCarIndex)
                            Text(
                                text = currentCar?.let { "${it.brand} ${it.model}" } ?: "No car selected",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                            currentCar?.let {
                                Text(
                                    text = it.number,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }
                    Icon(
                        painter = painterResource(id = com.example.drivocare.R.drawable.arrow),
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
                    cars.forEachIndexed { index, car ->
                        if (index != selectedCarIndex) {
                            Button(
                                onClick = {
                                    viewModel.selectedCarIndex.value = index
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
                                        painter = painterResource(id = com.example.drivocare.R.drawable.car),
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
                            addCarViewModel.reset()
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
                                painter = painterResource(id = com.example.drivocare.R.drawable.add),
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

            CalendarPage(
                month = currentMonth,
                eventDates = events.map {
                    it.endDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                },
                warningLightTitles = warningLights,
                eventTitleMap = events.associateBy(
                    { it.endDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() },
                    { it.title }
                ),
                onMonthChange = { direction ->
                    currentMonth = if (direction == "next") currentMonth.plusMonths(1) else currentMonth.minusMonths(1)
                },
                onDateSelected = { selectedDate = it },
                selectedDate = selectedDate
            )

            val todayEvents = events.filter {
                val date = it.endDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                date == selectedDate
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                todayEvents.forEach {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .width(8.dp)
                                .heightIn(min = 44.dp)
                                .background(Color(0xFF479195))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = it.title,
                                color = Color.Black,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp
                            )
                            if (it.description.isNotBlank()) {
                                Text(text = it.description, color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }
                if (cars.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable {
                                val selectedCarId = cars.getOrNull(selectedCarIndex)?.id
                                navController.navigate("addevent/$selectedCarId")
                            },d
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .width(8.dp)
                                .height(44.dp)
                                .background(Color(0xFF479195))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add event",
                            color = Color(0xFF479195),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .blur(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp),
                    color = Color(0xFF9C141E)
                )
            }
        }
    }
}
