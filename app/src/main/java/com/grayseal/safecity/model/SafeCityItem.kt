package com.grayseal.safecity.model

data class SafeCityItem(
    val Category: String,
    val Count: Int,
    val Latitude: Double,
    val Longitude: Double,
    val Reports: Int
)