package com.grayseal.safecity.data

import com.grayseal.safecity.R
import com.grayseal.safecity.navigation.SafeCityScreens

val navigationDrawerItems = listOf(
    NavigationDrawerItem(
        name = "Emergency Contacts",
        route = SafeCityScreens.ContactsScreen.name,
        icon = R.drawable.ic_contact
    ),
    NavigationDrawerItem(
        name = "Crime Statistics",
        route = SafeCityScreens.StatisticsScreen.name,
        icon = R.drawable.ic_statistics
    ),
    NavigationDrawerItem(
        name = "Crime Prevention Tips",
        route = SafeCityScreens.PreventionsScreen.name,
        icon = R.drawable.ic_tip
    ),
    NavigationDrawerItem(
        name = "Crime Hotspot Areas",
        route = SafeCityScreens.HotspotsScreen.name,
        icon = R.drawable.ic_danger
    )
)

