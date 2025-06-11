package com.example.drivocare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drivocare.viewmodel.AddCarViewModel
import com.example.drivocare.viewmodel.AddEventViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventPage(
    carId: String?,
    selectedDateArg: String?,
    navController: NavController,
    eventViewModel: AddEventViewModel,
    carViewModel: AddCarViewModel
) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val warningLights = listOf(
        "abs", "air_suspension", "airbag_indicator", "battery", "brake",
        "catalytic_converter", "check_engine", "engine_temperature",
        "fuel_filter", "glow_plug", "high_beam_light", "hood_open",
        "low_fuel", "master_warning", "oil_pressure", "parking_brake",
        "powertrain", "seat_belt", "tire_pressure", "traction_control",
        "traction_control_off", "transmission_temperature"
    )

    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedWarningLight by remember { mutableStateOf("") }
    LaunchedEffect(carId, selectedDateArg) {
        eventViewModel.reset()

        if (!selectedDateArg.isNullOrBlank()) {
            eventViewModel.startDate.value = selectedDateArg
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), Alignment.Center) {
                        Text("Add event", color = Color(0xFF479195))
                    }
                },
                navigationIcon = {
                    TextButton(
                        onClick = {
                            navController.navigate(if (carId == null) "addcar" else "mycars")
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Cancel", fontSize = 16.sp)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val result = eventViewModel.buildValidatedEvent(userId, carId ?: "")
                            if (result.isSuccess) {
                                val event = result.getOrNull()!!
                                if (carId == null) {
                                    carViewModel.addPendingEvent(event)
                                    Toast.makeText(context, "Event added to unsaved car", Toast.LENGTH_SHORT).show()
                                    navController.navigate("addcar")
                                } else {
                                    eventViewModel.saveEvent(
                                        event = event,
                                        onSuccess = {
                                            Toast.makeText(context, "Event added", Toast.LENGTH_SHORT).show()
                                            navController.navigate("mycars")
                                        },
                                        onError = {
                                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            } else {
                                Toast.makeText(context, result.exceptionOrNull()?.message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Save", fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(10.dp))

            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF479195))
                        .clickable { expanded = !expanded }
                        .padding(16.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            if (searchQuery.isEmpty()) "Search for a warning light" else searchQuery,
                            color = Color.White
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                if (expanded) {
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                            .background(Color.White)
                            .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                    ) {
                        items(warningLights.filter { it.contains(searchQuery, ignoreCase = true) }) { light ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedWarningLight = light
                                        eventViewModel.title.value = light
                                        expanded = false
                                    }
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF479195))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        light.replace("_", " ").replaceFirstChar { it.uppercaseChar() } + " warning light",
                                        color = Color(0xFF479195)
                                    )
                                }
                            }
                            Divider(color = Color.LightGray, thickness = 1.dp)
                        }
                    }
                }

                if (!expanded) {
                    Text("or", color = Color(0xFF479195), modifier = Modifier.padding(vertical = 8.dp))
                    OutlinedTextField(
                        value = eventViewModel.title.collectAsState().value,
                        onValueChange = { eventViewModel.title.value = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF479195),
                            unfocusedBorderColor = Color(0xFF479195)
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            EventDateTimeInputs(eventViewModel)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Set alert notification", color = Color.Red)
                Spacer(Modifier.width(8.dp))
                Checkbox(
                    checked = eventViewModel.notificationSet.collectAsState().value,
                    onCheckedChange = { eventViewModel.notificationSet.value = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Red)
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = eventViewModel.description.collectAsState().value,
                onValueChange = { eventViewModel.description.value = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF479195),
                    unfocusedBorderColor = Color(0xFF479195)
                )
            )
        }
    }
}

@Composable
private fun EventDateTimeInputs(viewModel: AddEventViewModel) {
    DateTimeRow("Starts", viewModel.startDate.collectAsState().value, viewModel.startTime.collectAsState().value, viewModel=viewModel) {
        viewModel.startDate.value = it.first
        viewModel.startTime.value = it.second
    }
    Divider(Modifier.padding(vertical = 8.dp), color = Color(0xFF479195))
    DateTimeRow("Ends", viewModel.endDate.collectAsState().value, viewModel.endTime.collectAsState().value, viewModel= viewModel) {
        viewModel.endDate.value = it.first
        viewModel.endTime.value = it.second
    }
}
@Composable
private fun DateTimeRow(
    label: String,
    date: String,
    time: String,
    viewModel: AddEventViewModel,
    onValueChange: (Pair<String, String>) -> Unit
) {
    val dateInteraction = remember { MutableInteractionSource() }
    val timeInteraction = remember { MutableInteractionSource() }

    var dateField by remember { mutableStateOf(TextFieldValue(date)) }
    LaunchedEffect(date) {
        if (dateField.text.isEmpty() && date.isNotBlank()) {
            dateField = TextFieldValue(date, TextRange(date.length))
        }
    }
    var timeField by remember { mutableStateOf(TextFieldValue(time)) }
    LaunchedEffect(time) {
        if (timeField.text.isEmpty() && time.isNotBlank()) {
            timeField = TextFieldValue(time, TextRange(time.length))
        }
    }
    val isDateFocused by dateInteraction.collectIsFocusedAsState()
    val isTimeFocused by timeInteraction.collectIsFocusedAsState()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color(0xFF479195), fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
        Spacer(Modifier.width(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = dateField,
                onValueChange = { newValue ->
                    val isDeleting = newValue.text.length < dateField.text.length
                    val digits = newValue.text.filter { it.isDigit() }.take(8)

                    val formatted = buildString {
                        digits.forEachIndexed { index, c ->
                            append(c)
                            if (index == 1 || index == 3) append('-')
                        }
                    }

                    val newCursor = when (digits.length) {
                        in 0..1 -> digits.length
                        in 2..3 -> digits.length + 1
                        in 4..8 -> digits.length + 2
                        else -> formatted.length
                    }

                    dateField = TextFieldValue(formatted, TextRange(newCursor))
                    onValueChange(formatted to timeField.text)
                },
                placeholder = {
                    if (!isDateFocused && date.isEmpty()) {
                        Text("DD-MM-YYYY", color = Color.White, fontSize = 12.sp, letterSpacing = 0.5.sp, maxLines = 1, softWrap = false, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                },
                interactionSource = dateInteraction,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF479195)),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.Gray,
                    focusedContainerColor = Color(0xFF479195),
                    unfocusedContainerColor = Color(0xFF479195),
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )

            TextField(
                value = timeField,
                onValueChange = { newValue ->
                    val isDeleting = newValue.text.length < timeField.text.length
                    val digits = newValue.text.filter { it.isDigit() }.take(4)

                    val formatted = buildString {
                        digits.forEachIndexed { index, c ->
                            append(c)
                            if (index == 1) append(':')
                        }
                    }

                    val newCursor = when (digits.length) {
                        in 0..1 -> digits.length
                        in 2..4 -> digits.length + 1
                        else -> formatted.length
                    }

                    timeField = TextFieldValue(formatted, TextRange(newCursor))
                    onValueChange(dateField.text to formatted)
                },
                placeholder = {
                    if (!isTimeFocused && time.isEmpty()) {
                        Text("HH:MM", color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                },
                interactionSource = timeInteraction,
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF479195)),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.Gray,
                    focusedContainerColor = Color(0xFF479195),
                    unfocusedContainerColor = Color(0xFF479195),
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )
        }
    }
}