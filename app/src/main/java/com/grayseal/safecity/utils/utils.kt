package com.grayseal.safecity.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient

// Extension function to convert a Drawable to a BitmapDrawable
fun Drawable.toBitmapDrawable(context: Context): BitmapDrawable {
    val bitmap = Bitmap.createBitmap(
        intrinsicWidth.takeIf { it > 0 } ?: 1,
        intrinsicHeight.takeIf { it > 0 } ?: 1,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return BitmapDrawable(context.resources, bitmap)
}

// Extension function to convert a BitmapDrawable to an ImageBitmap
fun BitmapDrawable.toImageBitmap(): ImageBitmap {
    return this.bitmap.asImageBitmap()
}

fun searchForPoliceStations(
    placesClient: PlacesClient,
    latitude: Double,
    longitude: Double
): Task<FindAutocompletePredictionsResponse> {
    val placesRequest = FindAutocompletePredictionsRequest.builder()
        .setLocationRestriction(
            RectangularBounds.newInstance(
                LatLng(latitude - 0.1, longitude - 0.1),
                LatLng(latitude + 0.1, longitude + 0.1)
            )
        )
        .setTypeFilter(TypeFilter.ESTABLISHMENT)
        .setQuery("police station")
        .build()

    return placesClient.findAutocompletePredictions(placesRequest)
}

fun calculateDistance(currentLocation: LatLng, policeStationLocation: LatLng): Double {
    val results = FloatArray(1)
    Location.distanceBetween(
        currentLocation.latitude, currentLocation.longitude,
        policeStationLocation.latitude, policeStationLocation.longitude, results
    )
    return results[0].toDouble()
}