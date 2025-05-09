package com.example.drivocare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.Comment
import com.example.drivocare.data.Post
import com.example.drivocare.repositories.PostRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

class PostDetailViewModel : ViewModel() {
    private val repository = PostRepository()
    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post> = _post

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    private val _commentText = MutableLiveData<String>("")
    val commentText: LiveData<String> = _commentText

    fun loadPost(postId: String) {
        repository.getPost(postId).addOnSuccessListener { document ->
            document.toObject(Post::class.java)?.let {
                _post.value = it
            }
        }
    }

    fun loadComments(postId: String) {
        repository.getComments(postId).observeForever {
            _comments.value = it
        }
    }

    fun setCommentText(text: String) {
        _commentText.value = text
    }

    fun addComment(postId: String) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null && !_commentText.value.isNullOrBlank()) {
            val comment = Comment(
                userId = currentUser.uid,
                username = currentUser.displayName ?: "User",
                text = _commentText.value ?: "",
                time = Timestamp.now()
            )

            repository.addComment(postId, comment).addOnSuccessListener {
                _commentText.value = ""
            }
        }
    }
}