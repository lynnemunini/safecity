package com.grayseal.safecity.screens.statistics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.grayseal.safecity.R
import com.grayseal.safecity.model.SafeCityItem
import com.grayseal.safecity.screens.main.StoreCoordinates
import com.grayseal.safecity.screens.main.StoreHotspotAreas
import com.grayseal.safecity.ui.theme.Green
import com.grayseal.safecity.ui.theme.poppinsFamily
import com.grayseal.safecity.utils.calculateDistance

@Composable
fun StatisticsScreen(navController: NavController) {
    val context = LocalContext.current
    // Retrieve nearbyHotspots
    val storeHotspotAreas = StoreHotspotAreas(context)
    val storeCoordinates = StoreCoordinates(context)
    var nearbyHotspots by remember { mutableStateOf(emptyList<SafeCityItem>()) }
    var loading by remember {
        mutableStateOf(true)
    }
    var coordinates by remember {
        mutableStateOf(Pair(0.0, 0.0))
    }
    LaunchedEffect(Unit) {
        val hotspots = storeHotspotAreas.retrieveNearbyHotspots()
        if (hotspots != null) {
            coordinates = storeCoordinates.retrieveCoordinates() ?: Pair(0.0, 0.0)
            nearbyHotspots = hotspots
            loading = false
        }
    }
    StatisticsScreenElements(
        navController = navController,
        nearbyHotspots = nearbyHotspots,
        loading = loading,
        latitude = coordinates.first,
        longitude = coordinates.second
    )
}

@Composable
fun StatisticsScreenElements(
    navController: NavController,
    nearbyHotspots: List<SafeCityItem>,
    loading: Boolean,
    latitude: Double,
    longitude: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = {
                        navController.popBackStack()
                    })
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Crime Statistics", fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Explore visualized crime statistics for areas within a 3 km radius of your location.",
                    fontFamily = poppinsFamily,
                    fontSize = 13.sp
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (loading) {
                LinearProgressIndicator(color = Green)
            } else {
                Hotspots(
                    navController = navController,
                    nearbyHotspots = nearbyHotspots,
                    latitude,
                    longitude
                )
            }
        }
    }
}

@Composable
fun Hotspots(
    navController: NavController,
    nearbyHotspots: List<SafeCityItem>,
    latitude: Double,
    longitude: Double
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(items = nearbyHotspots) { item: SafeCityItem ->
            val distance = calculateDistance(
                LatLng(latitude, longitude),
                LatLng(item.Latitude, item.Longitude)
            )
            Surface(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column() {
                        Text(item.LocationName)
                        Text(
                            (distance / 1000.0).toInt()
                                .toString() + " km away"
                        )
                    }
                }
            }
        }
    }
}