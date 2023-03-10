package com.grayseal.safecity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.grayseal.safecity.data.PoliceStation
import com.grayseal.safecity.location.PermissionDeniedContent
import com.grayseal.safecity.ui.theme.Orange
import com.grayseal.safecity.ui.theme.SafeCityTheme
import com.grayseal.safecity.ui.theme.poppinsFamily
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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
    val scope = rememberCoroutineScope()
    var isFabVisible by remember { mutableStateOf(true) }
    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val sheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
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
                    // To dismiss bottomSheet when clicking outside on the screen
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                if (sheetState.isExpanded) {
                                    sheetState.collapse()
                                }
                            })
                        },
                    floatingActionButton = {
                        if (isFabVisible) {
                            FloatingActionButton(
                                modifier = Modifier.padding(bottom = 120.dp),
                                onClick = {
                                    isFabVisible = false
                                    scope.launch {
                                        if (sheetState.isCollapsed) {
                                            sheetState.expand()
                                        }
                                    }
                                },
                                shape = CircleShape,
                                containerColor = Orange,
                                interactionSource = MutableInteractionSource()
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_add),
                                    contentDescription = "Add",
                                )
                            }
                        }
                    },
                    sheetContent = {
                                   BottomSheetContent(placesClient = placesClient, latitude = latitude, longitude = longitude)
                    },
                    content = { paddingValues ->
                        MapScreen(
                            modifier = Modifier.padding(paddingValues),
                            placesClient = placesClient,
                            latitude = latitude,
                            longitude = longitude,
                            context = context
                        )
                        if (sheetState.isExpanded) {
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
    LaunchedEffect(sheetState.isCollapsed) {
        if (sheetState.isCollapsed) {
            isFabVisible = true
        }
    }
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
    }
}

@Composable
fun BottomSheetContent(placesClient: PlacesClient, latitude: Double, longitude: Double){
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
                        val station = PoliceStation(markerOptions, prediction, place)
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
        modifier = Modifier.height(420.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
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
                .padding(top = 10.dp, bottom = 10.dp)
        )
        LazyColumn(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items (items = policeStations) {policeStation ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                        })
                ) {
                    Column {
                        androidx.compose.material3.Text(
                            policeStation.markerOptions.title.toString(),
                            fontFamily = poppinsFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        androidx.compose.material3.Text(
                            policeStation.markerOptions.snippet.toString(),
                            fontFamily = poppinsFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp)
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SafeCityTheme {
    }
}