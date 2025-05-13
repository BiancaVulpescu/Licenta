package com.example.drivocare.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.PostViewModel

@Composable
fun NewPostPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel,viewModel: PostViewModel) {
    val postText = viewModel.postText.observeAsState()
    val isPostCreated = viewModel.isPostCreated.observeAsState(false)
    val selectedImageUri = viewModel.selectedImageUri.observeAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setSelectedImageUri(it)
        }
    }

    LaunchedEffect(isPostCreated.value) {
        if (isPostCreated.value) {
            viewModel.resetPostCreatedState()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color(0xFFE0E0E0)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                TextField(
                    value = postText.value ?: "",
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

                selectedImageUri.value?.let {uri->
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2A9D8F)
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Add Image",
                            tint = Color.White
                        )
                    }

                    Button(
                        onClick = { viewModel.addPost() },
                        enabled = !postText.value.isNullOrBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE63946)
                        ),
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
