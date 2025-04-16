package com.example.drivocare.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drivocare.viewmodel.AddCarViewModel
import com.example.drivocare.viewmodel.AuthViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivocare.R


@Composable
fun AddCarPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, addCarViewModel: AddCarViewModel ) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = addCarViewModel.brand.value, onValueChange = { addCarViewModel.brand.value = it }, label = { Text("Brand") })
        OutlinedTextField(value = addCarViewModel.model.value, onValueChange = { addCarViewModel.model.value = it }, label = { Text("Model") })
        OutlinedTextField(value = addCarViewModel.year.value, onValueChange = { addCarViewModel.year.value = it }, label = { Text("Year") })
        OutlinedTextField(value = addCarViewModel.number.value, onValueChange = { addCarViewModel.number.value = it }, label = { Text("Number") })
        Text("Pending Events:", style = MaterialTheme.typography.titleMedium)
        addCarViewModel.pendingEvents.forEach { event ->
            Text("• ${event.title} (${event.startDate.toDate()} - ${event.endDate.toDate()})")
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {  navController.navigate("addevent") { launchSingleTop=true } },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF479195)),
            modifier = Modifier.wrapContentWidth(),
            shape = RectangleShape
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add Event/Document",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Add Event/Document",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(onClick = {
            addCarViewModel.saveCar(
                onSuccess = {carId->
                    Toast.makeText(context, "Car added", Toast.LENGTH_SHORT).show()
                    navController.navigate("mycars")
                },
                onError = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
        }) {
            Text("Save Car")
        }
    }
}


