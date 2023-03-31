package com.grayseal.safecity.model

import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place

data class PoliceStation(
    val markerOptions: MarkerOptions,
    val prediction: AutocompletePrediction,
    val place: Place,
    val distance: Double
)
