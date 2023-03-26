package com.grayseal.safecity.model

import kotlinx.serialization.Serializable

@Serializable
data class Categories(
    val assault: Int,
    val burglary: Int,
    val fraud: Int,
    val theft: Int,
    val vandalism: Int
)