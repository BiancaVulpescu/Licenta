package com.example.drivocare.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drivocare.data.AuthState
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.ScanningViewModel

@Composable
fun ScanningPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    scanningViewModel: ScanningViewModel = viewModel()
) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    val permissionState = scanningViewModel.hasCameraPermission.collectAsState()

    LaunchedEffect(Unit) {
        val hasPermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            scanningViewModel.updatePermissionState(true)
            scanningViewModel.startCamera(context, previewView, lifecycleOwner)
        } else {
            ActivityCompat.requestPermissions(
                context as android.app.Activity,
                arrayOf(Manifest.permission.CAMERA),
                0
            )
        }
    }

    DisposableEffect(Unit) {
        val listener = context as android.app.Activity
        val callback = object : ActivityCompat.OnRequestPermissionsResultCallback {
            override fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<out String>,
                grantResults: IntArray
            ) {
                if (requestCode == 0 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanningViewModel.updatePermissionState(true)
                    scanningViewModel.startCamera(context, previewView, lifecycleOwner)
                }
            }
        }
        ActivityCompat.requestPermissions(
            listener, arrayOf(Manifest.permission.CAMERA), 0
        )
        onDispose { }
    }

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (permissionState.value) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CaptureButton(
                    text = "Scan",
                    onClick = { scanningViewModel.capturePhoto(context) }
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Camera permission is required.", color = Color.Red)
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    ActivityCompat.requestPermissions(
                        context as android.app.Activity,
                        arrayOf(Manifest.permission.CAMERA),
                        0
                    )
                }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Composable
fun CaptureButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(80.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C141E))
    ) {
        Text(text = text, color = Color.White)
    }
}
