package com.example.drivocare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.Post
import com.example.drivocare.repositories.PostRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

class PostViewModel : ViewModel() {
    private val repository = PostRepository()
    private val auth = FirebaseAuth.getInstance()
    val posts: LiveData<List<Post>> = repository.getPosts()
    private val _isPostCreated = MutableLiveData<Boolean>(false)
    val isPostCreated: LiveData<Boolean> = _isPostCreated

    private val _postText = MutableLiveData<String>("")
    val postText: LiveData<String> = _postText

    private val _postImageUrl = MutableLiveData<String?>(null)
    val postImageUrl: LiveData<String?> = _postImageUrl

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    fun setPostText(text: String) {
        _postText.value = text
    }

    fun setPostImageUrl(url: String?) {
        _postImageUrl.value = url
    }

    fun addPost() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null && (!_postText.value.isNullOrBlank() || !_postImageUrl.value.isNullOrBlank())) {
            val post = Post(
                userId = currentUser.uid,
                username = currentUser.displayName ?: "User",
                contentText = _postText.value,
                imageUrl = _postImageUrl.value,
                time = Timestamp.now()
            )

            repository.addPost(post).addOnSuccessListener {
                _postText.value = ""
                _postImageUrl.value = null
                _isPostCreated.value = true
            }
        }
    }

    fun resetPostCreatedState() {
        _isPostCreated.value = false
    }
}