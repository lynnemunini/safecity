package com.grayseal.safecity.screens.main

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.grayseal.safecity.model.SafeCityItem
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StoreHotspotAreas(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hotspot_areas")

        private val HOTSPOTS_KEY = stringPreferencesKey("nearby_hotspots")
    }

    // Store nearbyHotspots
    suspend fun storeNearbyHotspots(nearbyHotspots: List<SafeCityItem>) {
        context.dataStore.edit { preferences ->
            preferences[HOTSPOTS_KEY] = Json.encodeToString(nearbyHotspots)
        }
    }

    // Retrieve nearByHotspots
    suspend fun retrieveNearbyHotspots(): List<SafeCityItem>? {
        return context.dataStore.data
            .mapNotNull { preferences ->
                preferences[HOTSPOTS_KEY]
            }
            .map { json ->
                Json.decodeFromString<List<SafeCityItem>>(json)
            }
            .firstOrNull()
    }
}