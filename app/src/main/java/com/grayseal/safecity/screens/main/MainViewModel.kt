package com.grayseal.safecity.screens.main

import androidx.lifecycle.ViewModel
import com.grayseal.safecity.data.DataOrException
import com.grayseal.safecity.model.SafeCityItem
import com.grayseal.safecity.repository.SafeCityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: SafeCityRepository) : ViewModel() {
    suspend fun getAllAreas(): DataOrException<ArrayList<SafeCityItem>, Boolean, Exception> {
        return repository.getAllAreas()
    }
}