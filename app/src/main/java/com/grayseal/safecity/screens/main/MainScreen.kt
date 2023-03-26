package com.grayseal.safecity.screens.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.*
import com.grayseal.safecity.BuildConfig
import com.grayseal.safecity.components.MiniFabItem
import com.grayseal.safecity.components.MultiFloatingActionButton
import com.grayseal.safecity.components.MultiFloatingState
import com.grayseal.safecity.data.DataOrException
import com.grayseal.safecity.data.PoliceStation
import com.grayseal.safecity.data.navigationDrawerItems
import com.grayseal.safecity.location.PermissionDeniedContent
import com.grayseal.safecity.model.SafeCityItem
import com.grayseal.safecity.navigation.Screen
import com.grayseal.safecity.ui.theme.Green
import com.grayseal.safecity.ui.theme.LightGreen
import com.grayseal.safecity.ui.theme.poppinsFamily
import com.grayseal.safecity.utils.*
import kotlinx.coroutines.*
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(navController: NavController) {
    GetCurrentLocation(navController = navController)
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun GetCurrentLocation(navController: NavController) {
    val viewModel: MainViewModel = hiltViewModel()

    val hotspotAreas = produceState<DataOrException<ArrayList<SafeCityItem>, Boolean, Exception>>(
        initialValue = DataOrException(loading = (true))
    ) {
        val areas = viewModel.getAllAreas().data
        if (areas != null) {
            value = viewModel.getAllAreas()
        }
    }.value.data

    val context = LocalContext.current
    Places.initialize(context, BuildConfig.MAPS_API_KEY)
    val placesClient = Places.createClient(context)
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val sheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val items = listOf(
        MiniFabItem(
            icon = ContextCompat.getDrawable(context, com.grayseal.safecity.R.drawable.ic_report)
                ?.toBitmapDrawable(context)!!.toImageBitmap(),
            label = "Report",
            identifier = "ReportFab"
        ),
        MiniFabItem(
            icon = ContextCompat.getDrawable(context, com.grayseal.safecity.R.drawable.ic_call)
                ?.toBitmapDrawable(context)!!.toImageBitmap(),
            label = "Call Police",
            identifier = "CallFab"
        )
    )
    var nearbyHotspots: List<SafeCityItem>? by remember {
        mutableStateOf(null)
    }
    var loading by remember {
        mutableStateOf(true)
    }
    val maxDistance = 2000 // Maximum distance in meters
    val scope = rememberCoroutineScope()
    val storeHotspotAreas = StoreHotspotAreas(context)
    val storeCoordinates = StoreCoordinates(context)

    LaunchedEffect(hotspotAreas) {
        scope.launch {
            nearbyHotspots =
                try {
                    retrieveHotspots(maxDistance, latitude, longitude, hotspotAreas)
                } catch (e: Exception) {
                    null
                }
            if (nearbyHotspots != null) {
                loading = false
                storeHotspotAreas.storeNearbyHotspots(nearbyHotspots!!)
                storeCoordinates.storeCoordinates(latitude, longitude)
            }
        }
    }


    com.grayseal.safecity.location.HandleRequest(
        permissionState = permissionState,
        deniedContent = { shouldShowRationale ->
            PermissionDeniedContent(
                rationaleMessage = "We apologize for the inconvenience, but unfortunately this " +
                        "permission is required to use the app",
                shouldShowRationale = shouldShowRationale
            ) { permissionState.launchPermissionRequest() }
        }
    ) {
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
        if (showMap && hotspotAreas != null && !loading) {
            nearbyHotspots?.let {
                SafeCityScaffold(
                    navController = navController,
                    placesClient = placesClient,
                    latitude = latitude,
                    longitude = longitude,
                    sheetScaffoldState = sheetScaffoldState,
                    drawerState = drawerState,
                    sheetState = sheetState,
                    nearbyHotspots = it,
                    fabItems = items,
                    scope = scope
                )
            }

        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LinearProgressIndicator(color = Green)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SafeCityScaffold(
    navController: NavController,
    placesClient: PlacesClient,
    latitude: Double,
    longitude: Double,
    sheetScaffoldState: BottomSheetScaffoldState,
    drawerState: DrawerState,
    sheetState: BottomSheetState,
    nearbyHotspots: List<SafeCityItem>,
    fabItems: List<MiniFabItem>,
    scope: CoroutineScope
) {
    var multiFloatingState by remember {
        mutableStateOf(MultiFloatingState.Collapsed)
    }
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
                drawerState = drawerState,
                onMultiFabStateChange = {
                    multiFloatingState = it
                },
                items = fabItems
            )
        },
        sheetContent = {
            BottomSheetContent(
                navController = navController,
                placesClient = placesClient,
                latitude = latitude,
                longitude = longitude
            )
        },
        content = {
            ModalNavigationDrawer(
                drawerState = drawerState,
                scrimColor = Color.Black.copy(alpha = 0.5f),
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.width(270.dp),
                        drawerShape = RectangleShape,
                        drawerContainerColor = Green,
                        drawerTonalElevation = 0.dp,
                    ) {
                        Spacer(Modifier.height(30.dp))
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(15.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.1f
                            )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    navigationDrawerItems.forEach {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp)
                                                .clickable(onClick = {
                                                    navController.navigate(it.route)
                                                }),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painter = painterResource(id = it.icon),
                                                contentDescription = it.name,
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(35.dp)
                                                    .padding(end = 10.dp)
                                            )
                                            Text(
                                                text = it.name,
                                                fontSize = 15.sp,
                                                fontFamily = poppinsFamily,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White,
                                                modifier = Modifier.align(Alignment.CenterVertically)
                                            )
                                        }
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "© 2023",
                                    fontSize = 12.sp,
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.align(Alignment.Bottom)
                                )
                            }
                        }
                    }
                },
                content = {
                    Box {
                        MapScreen(
                            latitude = latitude,
                            longitude = longitude,
                            hotspotAreas = nearbyHotspots,
                        ) {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                        // transparent overlay on top of content, shown if sheet is expanded
                        if (sheetState.isExpanded || multiFloatingState == MultiFloatingState.Expanded) {
                            Box(
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .fillMaxSize()
                            ) {}
                        }
                    }
                },
            )
        })
}

@Composable
fun MapScreen(
    latitude: Double,
    longitude: Double,
    hotspotAreas: List<SafeCityItem>,
    onMenuClick: () -> Unit,
) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }
    val location = LatLng(latitude, longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    // Mark HotSpot Areas
    var markers by remember { mutableStateOf(emptyList<MarkerOptions>()) }
    hotspotAreas.forEach { area ->
        val markerOptions = MarkerOptions()
            .position(LatLng(area.Latitude, area.Longitude))
            .title("CRIME HOTSPOT")
            .snippet(area.FrequentCrime.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
        markers += markerOptions
    }
    Map(
        properties = properties,
        uiSettings = uiSettings,
        markers = markers,
        cameraPositionState = cameraPositionState,
        onMenuClick = onMenuClick
    )
}

@Composable
fun Map(
    properties: MapProperties,
    uiSettings: MapUiSettings,
    markers: List<MarkerOptions>,
    cameraPositionState: CameraPositionState,
    onMenuClick: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            properties = properties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                // DO SOMETHING WHEN THE MAP LOADS
            }
        ) {
            markers.forEach { marker ->
                Marker(
                    state = MarkerState(
                        position = marker.position
                    ),
                    title = marker.title,
                    snippet = marker.snippet
                )
            }
        }
        Surface(
            modifier = Modifier.padding(20.dp),
            elevation = 10.dp,
            shape = CircleShape,
            color = Color.White
        ) {
            IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, CircleShape)
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                onClick = onMenuClick
            ) {
                Icon(
                    painter = painterResource(id = com.grayseal.safecity.R.drawable.ic_menu),
                    contentDescription = "Menu",
                    tint = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun BottomSheetContent(
    navController: NavController,
    placesClient: PlacesClient,
    latitude: Double,
    longitude: Double
) {
    var policeStations by remember { mutableStateOf(emptyList<PoliceStation>()) }
    val context = LocalContext.current
    // Search for police stations and add markers to the map
    // Call searchForPoliceStations only once using LaunchedEffect
    LaunchedEffect(true) {
        searchForPoliceStations(placesClient, latitude, longitude)
            .addOnSuccessListener { response ->
                response.autocompletePredictions.map { prediction ->
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
                            policeStations = policeStations.sortedBy { it.distance }
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
    Column(
        modifier = Modifier.height(400.dp),
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
                    .width(50.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(15.dp))
            )
        }
        Text(
            text = "Stations near You?",
            fontFamily = poppinsFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = 20.dp)

                .padding(top = 10.dp)
        )
        Text(
            text = "Are you looking to report a crime? Here's a list of police stations " +
                    "located near you.",
            fontFamily = poppinsFamily,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 4.dp, bottom = 15.dp)
        )
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Find Nearest Police Station
            val nearestPoliceStation = policeStations.firstOrNull()
            items(items = policeStations) { policeStation ->
                if (policeStation == nearestPoliceStation) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = LightGreen)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, top = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = com.grayseal.safecity.R.drawable.ic_location),
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 10.dp)
                            )
                            Column {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                policeStation.markerOptions.title.toString(),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                overflow = TextOverflow.Clip,
                                                fontFamily = poppinsFamily,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                modifier = Modifier.width(150.dp)
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(30.dp),
                                                color = Green,
                                                elevation = 4.dp,
                                            ) {
                                                Text(
                                                    "Recommended",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontFamily = poppinsFamily,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(
                                                        vertical = 4.dp,
                                                        horizontal = 10.dp
                                                    )
                                                )
                                            }
                                        }
                                        Text(
                                            policeStation.markerOptions.snippet.toString(),
                                            fontFamily = poppinsFamily,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.6f
                                            ),
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                ) {
                                    Text(
                                        (policeStation.distance / 1000.0).toInt()
                                            .toString() + " km away",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val geoUri = "http://maps.google.com/maps?daddr=" +
                                            "${policeStation.place.latLng?.latitude},${policeStation.place.latLng?.longitude}"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                                    ContextCompat.startActivity(context, intent, null)
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(width = 1.dp, color = Green),
                            ) {
                                Text(
                                    text = "Directions",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Green
                                )
                                Icon(
                                    painter = painterResource(id = com.grayseal.safecity.R.drawable.ic_directions),
                                    contentDescription = "Directions",
                                    tint = Green
                                )
                            }
                            Button(
                                onClick = {
                                    navController.navigate(
                                        route = Screen.ReportScreen.withArgs(
                                            policeStation.markerOptions.title.toString()
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Green),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Text(
                                    "Report",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                                Icon(
                                    painter = painterResource(id = com.grayseal.safecity.R.drawable.ic_report),
                                    contentDescription = "Report Here",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = com.grayseal.safecity.R.drawable.ic_location),
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 10.dp)
                        )
                        Column {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        policeStation.markerOptions.title.toString(),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        overflow = TextOverflow.Clip,
                                        fontFamily = poppinsFamily,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        policeStation.markerOptions.snippet.toString(),
                                        fontFamily = poppinsFamily,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    (policeStation.distance / 1000.0).toInt()
                                        .toString() + " km away",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                val geoUri = "http://maps.google.com/maps?daddr=" +
                                        "${policeStation.place.latLng?.latitude}," +
                                        "${policeStation.place.latLng?.longitude}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                                ContextCompat.startActivity(context, intent, null)
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(width = 1.dp, color = Green),
                        ) {
                            Text(
                                text = "Directions",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelSmall,
                                color = Green
                            )
                            Icon(
                                painter = painterResource(id = com.grayseal.safecity.R.drawable.ic_directions),
                                contentDescription = "Directions",
                                tint = Green
                            )
                        }
                        Button(
                            onClick = {
                                navController.navigate(
                                    route = Screen.ReportScreen.withArgs(
                                        policeStation.markerOptions.title.toString()
                                    )
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Green),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                "Report",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                            Icon(
                                painter = painterResource(id = com.grayseal.safecity.R.drawable.ic_report),
                                contentDescription = "Report Here",
                                tint = Color.White
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                    )
                }
            }
        }
    }
}

suspend fun retrieveHotspots(
    maxDistance: Int,
    latitude: Double,
    longitude: Double,
    hotspotAreas: ArrayList<SafeCityItem>?
): List<SafeCityItem>? {
    return withContext(Dispatchers.IO) {
        Log.d("HOTSPOT", "$hotspotAreas")
        val filteredSequence = hotspotAreas?.asSequence()
            ?.filter {
                checkDistanceBetween(
                    latitude,
                    longitude,
                    it.Latitude,
                    it.Longitude
                ) <= maxDistance
            }
        filteredSequence?.toList()
    }
}