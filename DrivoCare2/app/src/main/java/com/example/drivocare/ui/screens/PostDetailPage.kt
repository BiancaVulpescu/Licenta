package com.example.drivocare.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.drivocare.R
import com.example.drivocare.data.Comment
import com.example.drivocare.viewmodel.AuthViewModel
import com.example.drivocare.viewmodel.PostDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostDetailPage(
    modifier: Modifier = Modifier,
    postId: String,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: PostDetailViewModel
) {
    val username by authViewModel.currentUsername.collectAsState()
    val post by viewModel.post.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val commentText by viewModel.commentText.collectAsState()
    val selectedImageUri by viewModel.selectedCommentImageUri.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setSelectedCommentImageUri(uri)
    }

    fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
        viewModel.loadComments(postId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFCBD2D6))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            post?.let { currentPost ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.profile),
                                        contentDescription = "Profile",
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(currentPost.username, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    formatDate(currentPost.time.toDate()),
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            currentPost.contentText?.let { Text(it) }

                            currentPost.imageUrl?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                AsyncImage(
                                    model = it,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            items(comments) { comment ->
                CommentItem(comment = comment)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(10.dp)) {

                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(bottom = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = commentText,
                        onValueChange = { viewModel.setCommentText(it) },
                        placeholder = { Text("Add comment") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.LightGray,
                            unfocusedIndicatorColor = Color.LightGray
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Image", color = Color.White)
                    }

                    Button(
                        onClick = { viewModel.addComment(postId, username) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A9D8F)),
                        shape = RoundedCornerShape(0.dp),
                    ) {
                        Text("Post")
                    }
                }

                if (selectedImageUri != null) {
                    TextButton(
                        onClick = { viewModel.setSelectedCommentImageUri(null) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Remove Image", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp),
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile",
                        modifier = Modifier.size(30.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(comment.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Text(
                    formatDate(comment.time.toDate()),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.text, fontSize = 14.sp)

            comment.imageUrl?.let { imageUrl ->
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Comment Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
