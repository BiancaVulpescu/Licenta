package com.example.drivocare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.drivocare.data.WarningLight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WarningLightViewModel : ViewModel() {
    private val _warning = MutableStateFlow<WarningLight?>(null)
    val warning: StateFlow<WarningLight?> = _warning

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun loadWarningLightById(id: String) {
        val documentId = id
        Log.d("WarningLightVM", "Trying to fetch: WarningLights/$documentId")
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
            }
            .addOnFailureListener {
                Log.e("WarningLightVM", "Failed to fetch warning light: ${it.message}")
            }
    }
}
