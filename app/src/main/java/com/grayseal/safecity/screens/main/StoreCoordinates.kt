package com.grayseal.safecity.screens.main

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class StoreCoordinates(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "coordinates")

        private val LATITUDE_KEY = doublePreferencesKey("latitude")
        private val LONGITUDE_KEY = doublePreferencesKey("longitude")
    }

    suspend fun storeCoordinates(latitude: Double, longitude: Double) {
        context.dataStore.edit { preferences ->
            preferences[StoreCoordinates.LATITUDE_KEY] = latitude
            preferences[StoreCoordinates.LONGITUDE_KEY] = longitude
        }
    }

    suspend fun retrieveCoordinates(): Pair<Double, Double>? {
        return context.dataStore.data.map { preferences ->
            Pair(
                preferences[LATITUDE_KEY] ?: 0.0,
                preferences[LONGITUDE_KEY] ?: 0.0
            )
        }.firstOrNull()
    }
}