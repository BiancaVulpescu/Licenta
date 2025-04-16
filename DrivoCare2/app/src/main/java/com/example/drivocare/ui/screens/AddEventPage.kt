package com.example.drivocare.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drivocare.data.Event
import com.example.drivocare.viewmodel.AddCarViewModel
import com.example.drivocare.viewmodel.AddEventViewModel

@Composable
fun AddEventPage(carId: String?, navController: NavController, eventViewModel: AddEventViewModel = viewModel(),  carViewModel: AddCarViewModel) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = eventViewModel.title.value, onValueChange = { eventViewModel.title.value = it }, label = { Text("Title") })
        OutlinedTextField(value = eventViewModel.description.value, onValueChange = { eventViewModel.description.value = it }, label = { Text("Description (optional)") })
        OutlinedTextField(value = eventViewModel.startDate.value, onValueChange = { eventViewModel.startDate.value = it }, label = { Text("Start Date (dd-MM-yyyy)") })
        OutlinedTextField(value = eventViewModel.endDate.value, onValueChange = { eventViewModel.endDate.value = it }, label = { Text("End Date (dd-MM-yyyy)") })

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = eventViewModel.notificationSet.value,
                onCheckedChange = { eventViewModel.notificationSet.value = it }
            )
            Text("Set Notification")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
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
                                Log.d("Event added in pending", "event added in pending")
                                navController.navigate("addcar")
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }else  {
                    val error = result.exceptionOrNull()?.message ?: "Something went wrong"
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
        }) {
            Text("Save Event")
        }
    }
}

