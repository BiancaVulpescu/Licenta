package com.example.drivocare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drivocare.data.AuthState
import com.example.drivocare.viewmodel.AuthViewModel

@Composable
fun RegisterPage(modifier: Modifier=Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val authState by authViewModel.authState.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(authState) {
        when(authState){
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error-> Toast.makeText(context,
                (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else-> Unit
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF479195)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "DrivoCare",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Set Username", color = Color(0xFF50696A)) },
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color(0xFF50696A)) },
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color(0xFF50696A)) },
                modifier = Modifier.fillMaxWidth(0.8f),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password", color = Color(0xFF50696A)) },
                modifier = Modifier.fillMaxWidth(0.8f),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            errorMessage?.let {
                Text(it, color = Color.Red, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
            }

            TextButton(onClick = { navController.navigate("forgot_password")}) {
                Text("forgot password", color = Color.White)
            }
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (password == confirmPassword) {
                        authViewModel.register(email, password, username)
                    } else {
                        errorMessage = "Passwords do not match!"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF50696A)),
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Register", color = Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("I already have an account, Login", color = Color.White)
            }
        }
    }
}