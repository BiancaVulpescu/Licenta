package com.example.drivocare.ui.screens

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drivocare.data.AuthState
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.ScanningViewModel

@Composable
fun ScanningPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, scanningViewModel: ScanningViewModel= viewModel()) {
    val authState= authViewModel.authState.observeAsState()
    val context= LocalContext.current
    val previewView=remember{PreviewView(context)}
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login")
            else-> Unit
        }
    }
    LaunchedEffect(Unit) {
        scanningViewModel.startCamera(context, previewView)
    }
    Box(modifier=Modifier.fillMaxSize()){
        AndroidView(
            factory={previewView},
            modifier=Modifier.fillMaxSize()
        )
        Row(
            modifier=Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom=50.dp),
            horizontalArrangement=Arrangement.spacedBy(20.dp)
        ){
            CaptureButton(
                text="Take photo",
                onClick={scanningViewModel.capturePhoto(context)}
            )
        }
    }

}

@Composable
fun CaptureButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(100.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C141E))
    ) {
        Text(text = text, color = Color.White)
    }
}
