package com.example.drivocare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drivocare.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordPage(modifier: Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF479195)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Forgot Password", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(50.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email", color = Color(0xFF50696A)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = {
                authViewModel.sendPasswordResetEmail(
                    email,
                    onSuccess = {
                        message = "Reset email sent."
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                    },
                    onError = {
                        message = it
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF50696A))
        ) {
            Text("Send Reset Email", color = Color.White, fontWeight = FontWeight.Bold)
        }

        message?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(it, color = Color.White)
        }

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Back to Login", color = Color.White)
        }
    }
}
