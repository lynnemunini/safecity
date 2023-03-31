package com.grayseal.safecity.repository

import com.grayseal.safecity.model.DataOrException
import com.grayseal.safecity.model.SafeCityItem
import com.grayseal.safecity.network.SafeCityAPI
import javax.inject.Inject

class SafeCityRepository @Inject constructor(private val api: SafeCityAPI) {
    private val dataOrException = DataOrException<ArrayList<SafeCityItem>, Boolean, Exception>()

    suspend fun getAllAreas(): DataOrException<ArrayList<SafeCityItem>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            val areas = api.getAllAreas()
            dataOrException.data = areas
            if (dataOrException.data!!.isNotEmpty()) dataOrException.loading = false
        } catch (e: Exception) {
            dataOrException.e = e
        }
        return dataOrException
    }
}