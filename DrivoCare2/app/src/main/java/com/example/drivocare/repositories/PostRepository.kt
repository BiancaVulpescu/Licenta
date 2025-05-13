package com.example.drivocare.repositories

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.drivocare.data.Comment
import com.example.drivocare.data.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class PostRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    fun uploadImage(imageUri: Uri, onResult: (String?) -> Unit) {
        val imageRef = storage.child("post_images/${UUID.randomUUID()}.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    onResult(uri.toString())
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun addPost(post: Post): Task<Void> {
        val document = firestore.collection("posts").document()
        return document.set(post.copy(id = document.id))
    }
    fun getPost(postId: String): Task<DocumentSnapshot> {
        return firestore.collection("posts").document(postId).get()
    }
    fun getPosts(): LiveData<List<Post>> {
        val postsLiveData = MutableLiveData<List<Post>>()
        firestore.collection("posts")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                postsLiveData.value = snapshot?.toObjects(Post::class.java) ?: emptyList()
            }
        return postsLiveData
    }

    fun addComment(postId: String, comment: Comment): Task<DocumentReference> {
        return firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .add(comment)
    }

    fun getComments(postId: String): LiveData<List<Comment>> {
        val commentsLiveData = MutableLiveData<List<Comment>>()
        firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                commentsLiveData.value = snapshot?.toObjects(Comment::class.java) ?: emptyList()
            }
        return commentsLiveData
    }
}

