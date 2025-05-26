package com.example.drivocare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.PostViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun MyPostsPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: PostViewModel
) {
    val posts by viewModel.posts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val currentUserId = remember {
        FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    }

    val myPosts = remember(posts, currentUserId, searchQuery) {
        posts.filter {
            it.userId == currentUserId &&
                    (searchQuery.isBlank() || it.contentText?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFCBD2D6))
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search your posts", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 10.dp)
                .height(55.dp),
            shape = RoundedCornerShape(0.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color(0xFFB0B0B0),
                focusedIndicatorColor = Color(0xFFB0B0B0)
            ),
            singleLine = true
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Fills remaining space below TopNavBar
            contentAlignment = Alignment.Center
        ) {
            if (myPosts.isEmpty()) {
                Text("You have no posts yet.", fontSize = 18.sp)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    items(myPosts, key = { it.id }) { post ->
                        PostItem(post = post, navController = navController, viewModel = viewModel)
                    }
                }
            }
        }
    }
}
