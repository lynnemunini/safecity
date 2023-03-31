package com.grayseal.safecity.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.grayseal.safecity.model.DataOrException
import com.grayseal.safecity.model.Report
import com.grayseal.safecity.model.SafeCityItem
import com.grayseal.safecity.repository.SafeCityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: SafeCityRepository) : ViewModel() {
    suspend fun getAllAreas(): DataOrException<ArrayList<SafeCityItem>, Boolean, Exception> {
        return repository.getAllAreas()
    }

    fun getAllReports(callback: (List<Report>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("reports")
            .get()
            .addOnSuccessListener { result ->
                val reports = result.documents.mapNotNull { document ->
                    document.toObject<Report>()
                }
                callback(reports)
            }
            .addOnFailureListener { exception ->
                Log.e("REPORTS ERROR", "Error getting reports", exception)
                callback(emptyList())
            }
    }
}