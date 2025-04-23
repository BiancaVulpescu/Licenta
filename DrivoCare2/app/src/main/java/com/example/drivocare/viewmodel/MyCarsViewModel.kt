package com.example.drivocare.viewmodel
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.Car
import com.example.drivocare.data.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyCarsViewModel : ViewModel(){
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _cars =MutableLiveData<List<Car>>(emptyList())
    val cars: LiveData<List<Car>> get() =_cars
    
    private val _events = MutableLiveData<List<Event>>(emptyList())
    val events: LiveData<List<Event>> get() =_events

    var selectedCarIndex = mutableStateOf(0)

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
                if (selectedCarIndex.value >= carList.size) {
                    selectedCarIndex.value = 0
                }
                _cars.value=carList
            }
            .addOnFailureListener{
                _cars.value= emptyList()
            }
    }

    fun loadEventsForCar(carId: String){
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("cars")
            .document(carId)
            .collection("events")
            .get()
            .addOnSuccessListener { result ->
                val eventsList = result.documents.mapNotNull { it.toObject(Event::class.java) }
                _events.value = eventsList
            }
            .addOnFailureListener {
                _events.value = emptyList()
            }
    }
}