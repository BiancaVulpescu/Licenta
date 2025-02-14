package com.example.drivocare

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel:ViewModel() {
    private val auth: FirebaseAuth= FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore= FirebaseFirestore.getInstance()

    private val _authState=MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init{
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value=AuthState.Unauthenticated
        }else{
            _authState.value=AuthState.Authenticated
        }
    }
    fun login(email: String, password: String){
        if(email.isEmpty()||password.isEmpty()){
            _authState.value=AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value=AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    _authState.value=AuthState.Authenticated
                }else{
                    _authState.value=AuthState.Error(task.exception?.message?:"Something went wrong")
                }

            }
    }
    fun register(email: String, password: String, username: String){
        if(email.isEmpty() || password.isEmpty() || username.isEmpty()){
            _authState.value=AuthState.Error("Fields can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    var userId = auth.currentUser?.uid
                    if(userId !=null){
                        saveUserToFirestore(userId, email, username)
                    }
                    _authState.value=AuthState.Authenticated
                }else{
                    _authState.value=AuthState.Error(task.exception?.message?:"Something went wrong")
                }

            }
    }
    private fun saveUserToFirestore(userId: String, email: String, username: String){
        val user=mapOf(
            "email" to email,
            "username" to username
        )
        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
               println("User added to Firestore")
            }
            .addOnFailureListener{ e->
                println("Error adding user: ${e.message}")
            }
    }
    fun logout(){
        auth.signOut()
        _authState.value= AuthState.Unauthenticated
    }

}
sealed class AuthState{
    object Authenticated: AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String): AuthState()
}