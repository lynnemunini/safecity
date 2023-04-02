package com.grayseal.safecity.screens.hotspots

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.grayseal.safecity.ui.theme.poppinsFamily
import com.grayseal.safecity.utils.calculateDistance

@Composable
fun HotspotsScreen(navController: NavController) {
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
    HotspotsScreenElements(
        navController = navController,
        hotspotAreas = nearbyHotspots,
        loading = loading,
        latitude = coordinates.first,
        longitude = coordinates.second
    )
}

@Composable
fun HotspotsScreenElements(
    navController: NavController,
    hotspotAreas: List<SafeCityItem>,
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
                .padding(bottom = 10.dp),
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Crime HotSpots", fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Discover curated crime hotspots in your area to stay informed and safe.",
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
                LinearProgressIndicator(color = Color.White)
            } else {
                Hotspots(
                    nearbyHotspots = hotspotAreas,
                    latitude = latitude,
                    longitude = longitude
                )
            }
        }
    }
}

@Composable
fun Hotspots(nearbyHotspots: List<SafeCityItem>, latitude: Double, longitude: Double) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = nearbyHotspots) { item: SafeCityItem ->
            val distance = calculateDistance(
                LatLng(latitude, longitude),
                LatLng(item.Latitude, item.Longitude)
            )
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.building),
                        contentDescription = "Building",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(8.dp)
                            .clip(CircleShape)
                    )
                    Text(item.LocationName, fontWeight = FontWeight.Medium, fontFamily = poppinsFamily,
                        fontSize = 14.sp)
                }
                Text(
                    String.format("%.2f", (distance / 1000.0)) + " km away",
                    color = Color.DarkGray.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.note),
                        contentDescription = "Reports",
                        tint = Color(0xFF18775e),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Text(item.Reports.toInt().toString() + " reports", fontFamily = poppinsFamily,
                        fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.crime),
                        contentDescription = "Building",
                        tint = Color(0xFFe73058),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Text(item.FrequentCrime.capitalize(), fontFamily = poppinsFamily,
                        fontSize = 13.sp)
                }
                Divider(
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                )
            }
        }
    }
}