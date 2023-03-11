package com.grayseal.safecity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
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
import com.grayseal.safecity.data.PoliceStation
import com.grayseal.safecity.location.PermissionDeniedContent
import com.grayseal.safecity.ui.theme.Orange
import com.grayseal.safecity.ui.theme.SafeCityTheme
import com.grayseal.safecity.ui.theme.poppinsFamily
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

@OptIn(ExperimentalMaterialApi::class)
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
    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val sheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
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
                BottomSheetScaffold(
                    scaffoldState = sheetScaffoldState,
                    sheetElevation = 40.dp,
                    sheetBackgroundColor = Color.White,
                    sheetPeekHeight = 0.dp,
                    sheetShape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
                    floatingActionButton = {
                        MultiFloatingActionButton(
                            multiFloatingState = multiFloatingState,
                            sheetState = sheetState,
                            onMultiFabStateChange = {
                                multiFloatingState = it
                            },
                            items = items
                        )
                    },
                    sheetContent = {
                        BottomSheetContent(
                            placesClient = placesClient,
                            latitude = latitude,
                            longitude = longitude
                        )
                    },
                    content = { paddingValues ->
                        Box {
                            MapScreen(
                                modifier = Modifier.padding(paddingValues),
                                placesClient = placesClient,
                                latitude = latitude,
                                longitude = longitude,
                                context = context
                            )
                            // transparent overlay on top of content, shown if sheet is expanded
                            if (sheetState.isExpanded || multiFloatingState == MultiFloatingState.Expanded) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .fillMaxSize()
                                ) {}
                            }
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
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    val location = LatLng(latitude, longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 17f)
    }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    var markers by remember { mutableStateOf(emptyList<MarkerOptions>()) }

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
                        val markerOptionsList = response.autocompletePredictions.map { prediction ->
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
                                    Log.e(
                                        "RETRIEVING PLACES FAILED",
                                        "Error fetching place details: ${exception.message}"
                                    )
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            "POLICE STATIONS FAILED",
                            "Error searching for police stations: ${exception.message}"
                        )
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
        Surface(modifier = Modifier.padding(20.dp), elevation = 10.dp, shape = CircleShape, color = Color.White) {
            androidx.compose.material3.IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, CircleShape)
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Menu",
                    tint = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun BottomSheetContent(placesClient: PlacesClient, latitude: Double, longitude: Double) {
    var policeStations by remember { mutableStateOf(emptyList<PoliceStation>()) }
    // Search for police stations and add markers to the map
    searchForPoliceStations(placesClient, latitude, longitude)
        .addOnSuccessListener { response ->
            val markerOptionsList = response.autocompletePredictions.map { prediction ->
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
                        val distance = calculateDistance(
                            LatLng(latitude, longitude),
                            LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                        )
                        val station = PoliceStation(markerOptions, prediction, place, distance)
                        policeStations += station
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            "RETRIEVING PLACES FAILED",
                            "Error fetching place details: ${exception.message}"
                        )
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.e(
                "POLICE STATIONS FAILED",
                "Error searching for police stations: ${exception.message}"
            )
        }

    Column(
        modifier = Modifier.height(370.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            Divider(
                Modifier
                    .width(60.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(15.dp))
            )
        }
        androidx.compose.material3.Text(
            text = "Stations near You?",
            fontFamily = poppinsFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp)
        )
        androidx.compose.material3.Text(
            text = "Are you looking to report a crime? Here's a list of police stations located near you.",
            fontFamily = poppinsFamily,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 4.dp, bottom = 15.dp)
        )
        Divider(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
        )
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(items = policeStations) { policeStation ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        androidx.compose.material3.Text(
                            policeStation.markerOptions.title.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            overflow = TextOverflow.Clip,
                            fontFamily = poppinsFamily,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        androidx.compose.material3.Text(
                            policeStation.markerOptions.snippet.toString(),
                            fontFamily = poppinsFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                        androidx.compose.material3.Text(
                            (policeStation.distance / 1000.0).toInt().toString() + " km away",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Row {
                            androidx.compose.material3.Text(
                                "Report here",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelSmall,
                                color = Orange
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_report),
                                contentDescription = "Report Here",
                                tint = Orange
                            )
                        }
                    }
                }
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                )
            }
        }
    }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SafeCityTheme {
    }
}