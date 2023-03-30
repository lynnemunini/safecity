package com.grayseal.safecity.network

import com.grayseal.safecity.model.SafeCity
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface SafeCityAPI {
    @GET("data.json")
    suspend fun getAllAreas(): SafeCity
}