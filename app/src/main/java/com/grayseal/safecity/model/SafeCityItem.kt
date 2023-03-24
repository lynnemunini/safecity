package com.grayseal.safecity.model

data class SafeCityItem(
    val Categories: Categories,
    val Days: Days,
    val FrequentCrime: String,
    val Latitude: Double,
    val CrimeLikelihood: String,
    val Longitude: Double,
    val Months: Months,
    val NotoriousDay: String,
    val NotoriousMonth: String,
    val Reports: Double,
    val LocationName: String
)