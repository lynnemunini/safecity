package com.grayseal.safecity.screens.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.grayseal.safecity.R
import com.grayseal.safecity.model.SafeCityItem
import com.grayseal.safecity.navigation.Screen
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
    Surface(color = Green) {
        StatisticsScreenElements(
            navController = navController,
            nearbyHotspots = nearbyHotspots,
            loading = loading,
            latitude = coordinates.first,
            longitude = coordinates.second
        )
    }
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
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Back",
                tint = Color.White,
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
                    text = "Crime Statistics", fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )
                Text(
                    text = "Explore visualized crime statistics for areas within a 2 km radius of your location.",
                    fontFamily = poppinsFamily,
                    fontSize = 13.sp,
                    color = Color.White
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
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(items = nearbyHotspots) { item: SafeCityItem ->
            val distance = calculateDistance(
                LatLng(latitude, longitude),
                LatLng(item.Latitude, item.Longitude)
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.note),
                                contentDescription = "Report",
                                tint = Color(0xFF18775e),
                                modifier = Modifier.padding(8.dp)
                            )
                            Text("Reports: " + item.Reports.toInt().toString(), fontFamily = poppinsFamily,
                                fontSize = 13.sp)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.crime),
                                contentDescription = "Crime",
                                tint = Color(0xFFe73058),
                                modifier = Modifier.padding(8.dp)
                            )
                            Text("Frequent Crime: " + item.FrequentCrime.capitalize(), fontFamily = poppinsFamily,
                                fontSize = 13.sp)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.prediction),
                                contentDescription = "Prediction",
                                tint = Color(0xFFf3a52d),
                                modifier = Modifier.padding(8.dp)
                            )
                            Text("Crime Likelihood: " + item.CrimeLikelihood.capitalize(), fontFamily = poppinsFamily,
                                fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    String.format("%.2f", (distance / 1000.0)) + " km away",
                                    color = Color.DarkGray.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.End)
                                        .clickable(onClick = {
                                            navController.navigate(
                                                route = Screen.ChartsScreen.withArgs(
                                                    item.Id
                                                )
                                            )
                                        }),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Visualised Statistics",
                                        fontFamily = poppinsFamily,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        color = Green,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    androidx.compose.material.Icon(
                                        Icons.Rounded.ArrowForward,
                                        contentDescription = "Arrow",
                                        tint = Green,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}