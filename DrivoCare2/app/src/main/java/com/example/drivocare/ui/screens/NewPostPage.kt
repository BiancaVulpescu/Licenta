package com.example.drivocare.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.PostViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NewPostPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: PostViewModel
) {
    val postText by viewModel.postText.collectAsState()
    val isPostCreated by viewModel.isPostCreated.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val username by authViewModel.currentUsername.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    LaunchedEffect(Unit) {
        viewModel.setPostText("")
        viewModel.setSelectedImageUri(null)
        authViewModel.fetchCurrentUsername()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setSelectedImageUri(it) }
    }

    LaunchedEffect(isPostCreated) {
        if (isPostCreated) {
            viewModel.resetPostCreatedState()
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color(0xFFE0E0E0)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = postText,
                    onValueChange = { viewModel.setPostText(it) },
                    placeholder = { Text("Write your new post...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(4.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                selectedImageUri?.let { uri ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.setSelectedImageUri(null) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Remove Image", color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A9D8F)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Add Image",
                            tint = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            if (username.isNotBlank()) {
                                viewModel.addPost(username)
                            }
                        },
                        enabled = postText.isNotBlank() || selectedImageUri != null,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE63946)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Post",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
