package com.example.drivocare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.PostViewModel
import com.google.firebase.auth.FirebaseAuth

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

    Column(modifier = modifier.fillMaxSize()) {
        if (myPosts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("You have no posts yet.", fontSize = 18.sp)
            }
        } else {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search your posts...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                items(myPosts, key = { it.id }) { post ->
                    PostItem(post = post, navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}
