package com.grayseal.safecity.model

import kotlinx.serialization.Serializable

@Serializable
data class Days(
    val Friday: Int,
    val Monday: Int,
    val Saturday: Int,
    val Sunday: Int,
    val Thursday: Int,
    val Tuesday: Int,
    val Wednesday: Int
)