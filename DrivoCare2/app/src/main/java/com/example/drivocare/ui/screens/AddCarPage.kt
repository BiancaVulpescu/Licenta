package com.example.drivocare.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drivocare.viewmodel.AddCarViewModel
import com.example.drivocare.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
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
    val dateFormat = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }
    var isLoading by remember { mutableStateOf(false) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val brand by addCarViewModel.brand.collectAsState()
    val model by addCarViewModel.model.collectAsState()
    val year by addCarViewModel.year.collectAsState()
    val number by addCarViewModel.number.collectAsState()
    val isEditMode by addCarViewModel.isEditMode.collectAsState()
    val pendingEvents = addCarViewModel.pendingEvents

    Box(modifier = modifier.fillMaxSize()
        .background(Color(0xFFCBD2D6))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            Text("Brand", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF479195))
            OutlinedTextField(
                value = brand,
                onValueChange = { addCarViewModel.brand.value = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF479195),
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Model", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF479195))
            OutlinedTextField(
                value = model,
                onValueChange = { addCarViewModel.model.value = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF479195),
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("An productie", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF479195))
            OutlinedTextField(
                value = year,
                onValueChange = { addCarViewModel.year.value = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF479195),
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Nr. inmatriculare", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF479195))
            OutlinedTextField(
                value = number,
                onValueChange = { addCarViewModel.number.value = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF479195),
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isEditMode) {
                Text("Add events to calendar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF479195), modifier = Modifier.clickable {
                    navController.navigate("addevent") { launchSingleTop = true }
                })

                Spacer(modifier = Modifier.height(16.dp))

                pendingEvents.forEach { event ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(event.title, style = MaterialTheme.typography.titleMedium, color = Color(0xFF9C141E))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("data inceput", style = MaterialTheme.typography.bodySmall, color = Color(0xFF479195))
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.background(Color(0xFF479195)).padding(12.dp, 6.dp)) {
                                    Text(dateFormat.format(event.startDate.toDate()), style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.width(30.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("data expirare", style = MaterialTheme.typography.bodySmall, color = Color(0xFF479195))
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.background(Color(0xFF479195)).padding(12.dp, 6.dp)) {
                                    Text(dateFormat.format(event.endDate.toDate()), style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                }
                            }
                        }
                    }
                    Divider(color = Color(0xFF479195), thickness = 0.5.dp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = {
                        isLoading = true
                        if (!userId.isNullOrBlank()) {
                            addCarViewModel.saveCar(
                                userId = userId,
                                onSuccess = {
                                    isLoading = false
                                    addCarViewModel.reset()
                                    Toast.makeText(context, "Car saved", Toast.LENGTH_SHORT).show()
                                    navController.navigate("mycars")
                                },
                                onError = {
                                    isLoading = false
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            isLoading = false
                            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C141E)),
                    modifier = Modifier.width(140.dp),
                    shape = RectangleShape
                ) {
                    Text("Salveaza", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).blur(8.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center).size(50.dp),
                    color = Color(0xFF9C141E)
                )
            }
        }
    }
}
