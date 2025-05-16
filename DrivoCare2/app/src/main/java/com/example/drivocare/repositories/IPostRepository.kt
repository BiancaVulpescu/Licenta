package com.example.drivocare.repositories

import android.net.Uri
import com.example.drivocare.data.Comment
import com.example.drivocare.data.Post
import kotlinx.coroutines.flow.Flow

interface IPostRepository {
    suspend fun uploadImage(uri: Uri): String?
    fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (String) -> Unit)
    fun getPosts(): Flow<List<Post>>
    fun observeCommentCount(postId: String): Flow<Int>
    fun addComment(postId: String, comment: Comment, onSuccess: () -> Unit, onFailure: (String) -> Unit)
    fun getComments(postId: String): Flow<List<Comment>>
    fun getAllComments(): Flow<List<Comment>>
}
