package com.grayseal.safecity.model

data class Report(
    val policeStationName: String,
    val time: String,
    val date: String,
    val location: String,
    val typeOfCrime: String,
    val victimId: String,
    val victimName: String,
    val victimContact: String,
    val suspectName: String,
    val suspectDescription: String,
    val witnessName: String,
    val witnessContact: String,
    val description: String,
    val evidence: String,
    val otherInformation: String
){
    // Add a no-argument constructor
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )
}
