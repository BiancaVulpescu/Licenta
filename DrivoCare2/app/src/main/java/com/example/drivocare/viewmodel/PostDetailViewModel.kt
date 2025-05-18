package com.example.drivocare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.Comment
import com.example.drivocare.data.Post
import com.example.drivocare.repositories.PostRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewModelScope
import com.example.drivocare.repositories.IPostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostDetailViewModel(private val repository: IPostRepository) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText

    fun loadPost(postId: String) {
        viewModelScope.launch {
            repository.getPosts().collectLatest { posts ->
                _post.value = posts.find { it.id == postId }
            }
        }
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            repository.getComments(postId).collectLatest {
                _comments.value = it
            }
        }
    }

    fun setCommentText(text: String) {
        _commentText.value = text
    }

    fun addComment(postId: String, username: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val text = _commentText.value
        if (text.isBlank()) return

        val comment = Comment(
            userId = user.uid,
            username = username.ifBlank { "Username" },
            text = text,
            time = Timestamp.now(),
            postId = postId
        )

        repository.addComment(postId, comment, onSuccess = {
            _commentText.value = ""
        }, onFailure = {})
    }
}