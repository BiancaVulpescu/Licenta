package com.example.drivocare.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drivocare.R
import com.example.drivocare.data.Event
import com.example.drivocare.viewmodel.AddCarViewModel
import com.example.drivocare.viewmodel.AddEventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventPage(carId: String?, navController: NavController, eventViewModel: AddEventViewModel = viewModel(), carViewModel: AddCarViewModel) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Add event", 
                            color = Color(0xFF479195)
                        )
                    }
                },
                navigationIcon = {
                    TextButton(
                        onClick = { 
                            if (carId == null) {
                                navController.navigate("addcar")
                            } else {
                                navController.navigate("mycars")
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Cancel", fontSize = 16.sp)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val result = eventViewModel.buildValidatedEvent()
                            if (result.isSuccess) {
                                val event = result.getOrNull()!!
                                if (carId == null) {
                                    carViewModel.addPendingEvent(event)
                                    Toast.makeText(context, "Event added to unsaved car", Toast.LENGTH_SHORT).show()
                                    navController.navigate("addcar")
                                } else {
                                    eventViewModel.saveEvent(
                                        carId = carId,
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
                                val error = result.exceptionOrNull()?.message ?: "Something went wrong"
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Save", fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            
            OutlinedTextField(
                value = eventViewModel.title.value,
                onValueChange = { eventViewModel.title.value = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF479195),
                    focusedBorderColor = Color(0xFF479195),
                    unfocusedLabelColor = Color(0xFF479195),
                    focusedLabelColor = Color(0xFF479195)
                )
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Starts",
                    color = Color(0xFF479195),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(80.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = eventViewModel.startDate.value,
                        onValueChange = { eventViewModel.startDate.value = it },
                        placeholder = { 
                            Text(
                                "DD-MM-YYYY",
                                color = Color.White
                            ) 
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .background(Color(0xFF479195)),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            focusedContainerColor = Color(0xFF479195),
                            unfocusedContainerColor = Color(0xFF479195),
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                    )
                    TextField(
                        value = eventViewModel.startTime.value,
                        onValueChange = { eventViewModel.startTime.value = it },
                        placeholder = { 
                            Text(
                                "HH:MM",
                                color = Color.White
                            ) 
                        },
                        modifier = Modifier
                            .width(100.dp)
                            .height(50.dp)
                            .background(Color(0xFF479195)),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
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
            
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color(0xFF479195),
                thickness = 1.dp
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Ends",
                    color = Color(0xFF479195),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(80.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = eventViewModel.endDate.value,
                        onValueChange = { eventViewModel.endDate.value = it },
                        placeholder = { 
                            Text(
                                "DD-MM-YYYY",
                                color = Color.White
                            ) 
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .background(Color(0xFF479195)),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            focusedContainerColor = Color(0xFF479195),
                            unfocusedContainerColor = Color(0xFF479195),
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                    )
                    TextField(
                        value = eventViewModel.endTime.value,
                        onValueChange = { eventViewModel.endTime.value = it },
                        placeholder = { 
                            Text(
                                "HH:MM",
                                color = Color.White
                            ) 
                        },
                        modifier = Modifier
                            .width(100.dp)
                            .height(50.dp)
                            .background(Color(0xFF479195)),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Set alert notification",
                    color = Color.Red,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = eventViewModel.notificationSet.value,
                    onCheckedChange = { eventViewModel.notificationSet.value = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Red,
                        uncheckedColor = Color.Red
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = eventViewModel.description.value,
                onValueChange = { eventViewModel.description.value = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF479195),
                    focusedBorderColor = Color(0xFF479195),
                    unfocusedLabelColor = Color(0xFF479195),
                    focusedLabelColor = Color(0xFF479195)
                )
            )
        }
    }
}

