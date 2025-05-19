package com.example.drivocare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.WarningLight
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WarningLightViewModel : ViewModel() {
    private val _warning = MutableStateFlow<WarningLight?>(null)
    val warning: StateFlow<WarningLight?> = _warning

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadWarningLightById(id: String) {
        val documentId = id
        _isLoading.value = true
         firestore.collection("WarningLights").document(documentId).get()
            .addOnSuccessListener { doc ->
                if(doc.exists()){

                    val symbolName = doc.getString("SymbolName") ?: ""
                    val description = doc.getString("Description") ?: ""
                    val fixDescription = doc.getString("FixDescription") ?: ""

                    val imageRef = storage.reference.child("WarningLights/$documentId.jpg")

                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        _warning.value = WarningLight(
                            imageUrl = uri.toString(),
                            symbolName = symbolName,
                            description = description,
                            fixDescription = fixDescription
                        )
                    }
                }
                _isLoading.value = false
            }
            .addOnFailureListener {
                Log.e("WarningLightVM", "Failed to fetch warning light: ${it.message}")
                _isLoading.value = false
            }
    }
}
