package com.grayseal.safecity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.*
import com.grayseal.safecity.BuildConfig.MAPS_API_KEY
import com.grayseal.safecity.components.MiniFabItem
import com.grayseal.safecity.components.MultiFloatingActionButton
import com.grayseal.safecity.components.MultiFloatingState
import com.grayseal.safecity.location.PermissionDeniedContent
import com.grayseal.safecity.ui.theme.Orange
import com.grayseal.safecity.ui.theme.SafeCityTheme
import com.grayseal.safecity.utils.toBitmapDrawable
import com.grayseal.safecity.utils.toImageBitmap

class MainActivity : ComponentActivity() {
    private lateinit var placesClient: PlacesClient

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(this, MAPS_API_KEY)
        placesClient = Places.createClient(this)
        setContent {
            SafeCityTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GetCurrentLocation(context = this, placesClient)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun GetCurrentLocation(
    context: Context,
    placesClient: PlacesClient
) {
    val permissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    // To create an instance of the fused Location Provider Client
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    var latitude by remember {
        mutableStateOf(0.0)
    }
    var longitude by remember {
        mutableStateOf(0.0)
    }
    var showMap by remember {
        mutableStateOf(false)
    }
    var multiFloatingState by remember {
        mutableStateOf(MultiFloatingState.Collapsed)
    }
    val items = listOf(
        MiniFabItem(
            icon = ContextCompat.getDrawable(context, R.drawable.ic_report)
                ?.toBitmapDrawable(context)!!.toImageBitmap(),
            label = "Report",
            identifier = "ReportFab"
        ),
        MiniFabItem(
            icon = ContextCompat.getDrawable(context, R.drawable.ic_call)
                ?.toBitmapDrawable(context)!!.toImageBitmap(),
            label = "Call Police",
            identifier = "CallFab"
        )
    )
    com.grayseal.safecity.location.HandleRequest(
        permissionState = permissionState,
        deniedContent = { shouldShowRationale ->
            PermissionDeniedContent(
                rationaleMessage = "We apologize for the inconvenience, but unfortunately this " +
                        "permission is required to use the app",
                shouldShowRationale = shouldShowRationale
            ) { permissionState.launchPermissionRequest() }
        },
        content = {
            // Check to see if permission is available
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                try {
                    val locationResult = fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    )

                    locationResult.addOnSuccessListener { location: Location? ->
                        // Get location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.latitude
                            longitude = location.longitude
                            showMap = true
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error Fetching Location", Toast.LENGTH_LONG).show()
                }
            }
            if (showMap) {
                Scaffold(floatingActionButton = {
                    MultiFloatingActionButton(
                        multiFloatingState = multiFloatingState,
                        onMultiFabStateChange = {
                            multiFloatingState = it
                        },
                        items = items
                    )
                }, content = { paddingValues ->
                    MapScreen(
                        modifier = Modifier.padding(paddingValues),
                        placesClient = placesClient,
                        latitude = latitude,
                        longitude = longitude,
                        context = context
                    )
                    if (multiFloatingState == MultiFloatingState.Expanded) {
                        Box(
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.65f))
                                .fillMaxSize()
                        ) {}
                    }
                })
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LinearProgressIndicator(color = Orange)
                }
            }
        }
    )
}

@Composable
fun MapScreen(
    modifier: Modifier,
    placesClient: PlacesClient,
    latitude: Double,
    longitude: Double,
    context: Context
) {
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    val location = LatLng(latitude, longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 12f)
    }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    var markers by remember { mutableStateOf(emptyList<MarkerOptions>()) }
    var showMarker by remember {
        mutableStateOf(false)
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            properties = properties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                // Search for police stations and add markers to the map
                searchForPoliceStations(placesClient, latitude, longitude)
                    .addOnSuccessListener { response ->
                        val markerOptionsList = response.autocompletePredictions.map{ prediction ->
                            val placeId = prediction.placeId
                            val placeFields = listOf(Place.Field.LAT_LNG)
                            val request = FetchPlaceRequest.builder(placeId, placeFields).build()
                            placesClient.fetchPlace(request)
                                .addOnSuccessListener { response ->
                                    val place = response.place
                                    val latLng = place.latLng
                                    val markerOptions = MarkerOptions()
                                        .position(LatLng(latLng!!.latitude, latLng.longitude))
                                        .title(prediction.getPrimaryText(null).toString())
                                        .snippet(prediction.getSecondaryText(null).toString())
                                    markers += markerOptions
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("RETRIEVING PLACES FAILED", "Error fetching place details: ${exception.message}")
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("POLICE STATIONS FAILED", "Error searching for police stations: ${exception.message}")
                    }
            }
        ) {
            markers.forEach { marker ->
                Marker(
                    state = MarkerState(
                        position = marker.position,
                    ),
                    title = marker.title,
                    snippet = marker.snippet
                )
            }
        }
        Switch(
            checked = uiSettings.zoomControlsEnabled,
            onCheckedChange = {
                uiSettings = uiSettings.copy(zoomControlsEnabled = it)
            }
        )
    }
}

fun searchForPoliceStations(placesClient: PlacesClient, latitude: Double, longitude: Double): Task<FindAutocompletePredictionsResponse> {
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SafeCityTheme {
    }
}