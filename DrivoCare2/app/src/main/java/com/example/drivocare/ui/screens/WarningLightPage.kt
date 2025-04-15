package com.example.drivocare.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drivocare.R
import com.example.drivocare.viewmodel.WarningLightViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun WarningLightPage(id: String, viewModel: WarningLightViewModel = viewModel()) {
    val warning by viewModel.warning.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadWarningLightById(id)
    }

    warning?.let { light ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF479195))
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { /* TODO: Add calendar functionality */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF479195)),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Add to calendar",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to calendar", color = Color.White)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(light.imageUrl),
                    contentDescription = light.symbolName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Fit
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = light.symbolName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C141E)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = light.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "How to fix",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = light.fixDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray
                )
            }
        }
    } ?: Text("Loading...")
}