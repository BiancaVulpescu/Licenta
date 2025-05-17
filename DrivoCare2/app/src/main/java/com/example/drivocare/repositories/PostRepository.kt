package com.example.drivocare.repositories

import android.net.Uri
import com.example.drivocare.data.Comment
import com.example.drivocare.data.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID

class PostRepository : IPostRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    override suspend fun uploadImage(uri: Uri): String? = suspendCancellableCoroutine { cont ->
        val imageRef = storageRef.child("post_images/${UUID.randomUUID()}.jpg")
        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    cont.resume(downloadUri.toString(), null)
                }
            }
            .addOnFailureListener {
                cont.resume(null, null)
            }
    }

    override fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val document = firestore.collection("posts").document()
        document.set(post.copy(id = document.id))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to add post") }
    }

    override fun getPosts(): Flow<List<Post>> = callbackFlow {
        val listener = firestore.collection("posts")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObjects(Post::class.java) ?: emptyList())
            }

        awaitClose { listener.remove() }
    }

    override fun addComment(postId: String, comment: Comment, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .add(comment)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to add comment") }
    }

    override fun getComments(postId: String): Flow<List<Comment>> = callbackFlow {
        val listener = firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObjects(Comment::class.java) ?: emptyList())
            }

        awaitClose { listener.remove() }
    }
    override fun getAllComments(): Flow<List<Comment>> = callbackFlow {
        val listener = firestore.collectionGroup("comments")
            .addSnapshotListener { snapshot, _ ->
                val commentList = snapshot?.documents?.mapNotNull { doc ->
                    val comment = doc.toObject(Comment::class.java)
                    val postId = doc.reference.path.split("/")[1]
                    comment?.copy(postId = postId)
                } ?: emptyList()
                trySend(commentList)
            }
        awaitClose { listener.remove() }
    }


    override fun observeCommentCount(postId: String): Flow<Int> = callbackFlow {
        val listener = firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.size() ?: 0)
            }

        awaitClose { listener.remove() }
    }


}

