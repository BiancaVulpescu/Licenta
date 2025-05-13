package com.example.drivocare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drivocare.viewmodel.AuthViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import com.example.drivocare.viewmodel.PostDetailViewModel
import com.example.drivocare.viewmodel.PostViewModel

@Composable
fun MyPostsPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, viewModel: PostViewModel) {
    val postsState = viewModel.posts.observeAsState(initial = emptyList())
    val myPosts = postsState.value.filter { it.userId == viewModel.getCurrentUserId() }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
