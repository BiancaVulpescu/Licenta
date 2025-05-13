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

    val posts: LiveData<List<Post>> = repository.getPosts()

    private val _isPostCreated = MutableLiveData(false)
    val isPostCreated: LiveData<Boolean> = _isPostCreated

    private val _postText = MutableLiveData("")
    val postText: LiveData<String> = _postText

    private val _selectedImageUri = MutableLiveData<Uri?>(null)
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val commentCounts = mutableMapOf<String, MutableLiveData<Int>>()

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

    fun addPost(username:String) {
        val currentUser = auth.currentUser ?: return
        val text = _postText.value
        val imageUri = _selectedImageUri.value
        val userId = currentUser.uid

        if (text.isNullOrBlank() && imageUri == null) return
        val postToFirestore = { imageUrl: String? ->
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
        if (imageUri != null) {
            repository.uploadImage(imageUri) { imageUrl ->
                if (imageUrl != null) {
                    postToFirestore(imageUrl)
                } else {
                    Log.e("PostViewModel", "Image upload failed. Post not created.")
                }
            }
        } else {
            postToFirestore( null)
        }
    }
    fun clearDraft() {
        _postText.value = ""
        _selectedImageUri.value = null
    }

    fun getCommentCountLive(postId: String): LiveData<Int> {
        if (!commentCounts.containsKey(postId)) {
            val liveData = repository.observeCommentCount(postId)
            commentCounts[postId] = liveData as MutableLiveData<Int>
        }
        return commentCounts[postId]!!
    }


}
