package com.example.drivocare.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.Post
import com.example.drivocare.repositories.PostRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostViewModel : ViewModel() {
    private val repository = PostRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _cachedUsername = MutableLiveData<String?>()
    val cachedUsername: LiveData<String?> = _cachedUsername

    val posts: LiveData<List<Post>> = repository.getPosts()

    private val _isPostCreated = MutableLiveData(false)
    val isPostCreated: LiveData<Boolean> = _isPostCreated

    private val _postText = MutableLiveData("")
    val postText: LiveData<String> = _postText

    private val _selectedImageUri = MutableLiveData<Uri?>(null)
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    fun setPostText(text: String) {
        _postText.value = text
    }

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun resetPostCreatedState() {
        _isPostCreated.value = false
    }
    fun loadUsernameIfNeeded() {
        if (_cachedUsername.value != null) return

        val userId = auth.currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                _cachedUsername.value = document.getString("username") ?: "User"
            }
            .addOnFailureListener { e ->
                Log.e("PostViewModel", "Failed to fetch username: ${e.message}", e)
            }
    }

    fun addPost() {
        val currentUser = auth.currentUser ?: return
        val text = _postText.value
        val imageUri = _selectedImageUri.value
        val username = _cachedUsername.value ?: "Username"
        val userId = currentUser.uid

        if (text.isNullOrBlank() && imageUri == null) return

        if (imageUri != null) {
            repository.uploadImage(imageUri) { imageUrl ->
                if (imageUrl != null) {
                    postToFirestore(userId, username, text, imageUrl)
                } else {
                    Log.e("PostViewModel", "Image upload failed. Post not created.")
                }
            }
        } else {
            postToFirestore(userId, username, text, null)
        }
    }

    private fun postToFirestore(userId: String, username: String, text: String?, imageUrl: String?) {
        val post = Post(
            userId = userId,
            username = username,
            contentText = text,
            imageUrl = imageUrl,
            time = Timestamp.now()
        )
        repository.addPost(post).addOnSuccessListener {
            _postText.value = ""
            _selectedImageUri.value = null
            _isPostCreated.value = true
        }.addOnFailureListener { e ->
            Log.e("PostViewModel", "Failed to save post: ${e.message}", e)
        }
    }
}
