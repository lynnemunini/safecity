package com.grayseal.safecity.model

import kotlinx.serialization.Serializable

@Serializable
data class Months(
    val April: Int,
    val August: Int,
    val December: Int,
    val February: Int,
    val January: Int,
    val July: Int,
    val June: Int,
    val March: Int,
    val May: Int,
    val November: Int,
    val October: Int,
    val September: Int
)