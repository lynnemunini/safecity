package com.grayseal.safecity.screens.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grayseal.safecity.data.DataOrException
import com.grayseal.safecity.model.SafeCityItem
import com.grayseal.safecity.repository.SafeCityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: SafeCityRepository) : ViewModel() {
    var resultsState: MutableState<DataOrException<ArrayList<SafeCityItem>, Boolean, Exception>> =
        mutableStateOf(DataOrException(arrayListOf(), false, Exception("")))

    var hotspotAreas: MutableState<ArrayList<SafeCityItem>> = mutableStateOf(arrayListOf())
    var loading: MutableState<Boolean> = mutableStateOf(false)

    fun getAllAreas() {
        viewModelScope.launch {
            loading.value = true
            resultsState.value = repository.getAllAreas()
            if (resultsState.value.data != null) {
                hotspotAreas.value = resultsState.value.data!!
                loading.value = false
            }
        }
    }
}