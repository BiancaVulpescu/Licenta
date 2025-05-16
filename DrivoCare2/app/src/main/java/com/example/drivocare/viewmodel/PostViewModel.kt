package com.example.drivocare.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.drivocare.usecase.AddPostUseCase
import androidx.lifecycle.viewModelScope
import com.example.drivocare.data.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.drivocare.repositories.IPostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine

class PostViewModel(
    private val addPostUseCase: AddPostUseCase,
    private val repository: IPostRepository
) : ViewModel() {
    private val _postText = MutableStateFlow("")
    val postText: StateFlow<String> = _postText

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _isPostCreated = MutableStateFlow(false)
    val isPostCreated: StateFlow<Boolean> = _isPostCreated

    val posts: StateFlow<List<Post>> = repository.getPosts()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredPosts: StateFlow<List<Post>> = combine(_searchQuery, posts) { query, allPosts ->
        if (query.isBlank()) allPosts
        else allPosts.filter { it.contentText?.contains(query, ignoreCase = true) == true }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPostText(text: String) {
        _postText.value = text
    }

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun clearPost() {
        _postText.value = ""
        _selectedImageUri.value = null
    }

    fun addPost(username: String) {
        viewModelScope.launch {
            addPostUseCase(
                username = username,
                contentText = _postText.value,
                imageUri = _selectedImageUri.value,
                onSuccess = {
                    clearPost()
                    _isPostCreated.value = true
                },
                onFailure = {
                    Log.e("PostViewModel", it)
                }
            )
        }
    }
    fun getCommentCountFlow(postId: String): Flow<Int> {
        return repository.observeCommentCount(postId)
    }
    fun resetPostCreatedState() {
        _isPostCreated.value = false
    }
}
