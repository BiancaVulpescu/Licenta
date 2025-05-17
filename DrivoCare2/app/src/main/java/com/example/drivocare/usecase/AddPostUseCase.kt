package com.example.drivocare.usecase

import android.net.Uri
import com.example.drivocare.data.Post
import com.example.drivocare.repositories.IPostRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

class AddPostUseCase(private val repository: IPostRepository) {
    suspend operator fun invoke(
        username: String,
        contentText: String?,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (contentText.isNullOrBlank() && imageUri == null) {
            onFailure("Post must have content or an image.")
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrBlank()) {
            onFailure("User is not authenticated.")
            return
        }

        val imageUrl = if (imageUri != null) {
            repository.uploadImage(imageUri)
        } else null

        val post = Post(
            userId = userId,
            username = username,
            contentText = contentText,
            imageUrl = imageUrl,
            time = Timestamp.now()
        )

        repository.addPost(post, onSuccess, onFailure)
    }
}
