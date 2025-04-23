package com.example.drivocare.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.Car
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyCarsViewModel : ViewModel(){
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _cars =MutableLiveData<List<Car>>(emptyList())
    val cars: LiveData<List<Car>> get() =_cars

    fun loadCarsForCurrentUser() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("cars")
            .get()
            .addOnSuccessListener { result->
                val carList=result.documents.mapNotNull {doc->
                    val car = doc.toObject(Car::class.java)
                    car?.copy(id = doc.id)
                }
                Log.d("MyCarsViewModel", "Loaded ${carList.size} cars")
                _cars.value=carList
            }
            .addOnFailureListener{
                Log.e("MyCarsViewModel", "Error loading cars: ${it.message}")
                _cars.value= emptyList()
            }
    }
}