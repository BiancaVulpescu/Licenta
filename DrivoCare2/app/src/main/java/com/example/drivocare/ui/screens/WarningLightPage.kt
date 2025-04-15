package com.example.drivocare.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivocare.viewmodel.WarningLightViewModel
import coil.compose.rememberAsyncImagePainter
@Composable
fun WarningLightPage(id:String, viewModel: WarningLightViewModel = viewModel()) {
    val warning by viewModel.warning.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadWarningLightById(id)
    }

    warning?.let { light ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = rememberAsyncImagePainter(light.imageUrl),
                contentDescription = light.symbolName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(light.symbolName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(light.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Fix: ${light.fixDescription}", style = MaterialTheme.typography.bodySmall)
        }
    } ?: Text("Loading...")
}