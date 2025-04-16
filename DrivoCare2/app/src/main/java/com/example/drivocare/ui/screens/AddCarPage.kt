package com.example.drivocare.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drivocare.R
import com.example.drivocare.viewmodel.AddCarViewModel
import com.example.drivocare.viewmodel.AuthViewModel
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    addCarViewModel: AddCarViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Brand field
            Text(
                "Brand",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF479195)
            )
            OutlinedTextField(
                value = addCarViewModel.brand.value,
                onValueChange = { addCarViewModel.brand.value = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF479195),
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Model field
            Text(
                "Model",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF479195)
            )
            OutlinedTextField(
                value = addCarViewModel.model.value,
                onValueChange = { addCarViewModel.model.value = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF479195),
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Production Year
            Text(
                "An productie",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF479195)
            )
            OutlinedTextField(
                value = addCarViewModel.year.value,
                onValueChange = { addCarViewModel.year.value = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF479195),
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Registration Number
            Text(
                "Nr. inmatriculare",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF479195)
            )
            OutlinedTextField(
                value = addCarViewModel.number.value,
                onValueChange = { addCarViewModel.number.value = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF479195),
                    unfocusedContainerColor = Color.White
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Add events to calendar",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF479195),
                modifier = Modifier.clickable {
                    navController.navigate("addevent") { launchSingleTop = true }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Event list
            addCarViewModel.pendingEvents.forEach { event ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF9C141E)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "data inceput",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF479195)
                            )
                            Text(
                                dateFormat.format(event.startDate.toDate()),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                        }
                        Column {
                            Text(
                                "data expirare",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF479195)
                            )
                            Text(
                                dateFormat.format(event.endDate.toDate()),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
                Divider(color = Color(0xFF479195), thickness = 0.5.dp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Save button
            Button(
                onClick = {
                    isLoading = true
                    addCarViewModel.saveCar(
                        onSuccess = { carId ->
                            isLoading = false
                            Toast.makeText(context, "Car added", Toast.LENGTH_SHORT).show()
                            navController.navigate("mycars")
                        },
                        onError = {
                            isLoading = false
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C141E)),
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Text(
                    "Salveaza",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
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


