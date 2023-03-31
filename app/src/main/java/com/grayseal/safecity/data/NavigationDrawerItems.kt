package com.grayseal.safecity.data

import com.grayseal.safecity.R
import com.grayseal.safecity.model.NavigationDrawerItem
import com.grayseal.safecity.navigation.Screen

val navigationDrawerItems = listOf(
    NavigationDrawerItem(
        name = "Crime Statistics",
        route = Screen.StatisticsScreen.route,
        icon = R.drawable.ic_statistics
    ),
    NavigationDrawerItem(
        name = "Crime Prevention Tips",
        route = Screen.PreventionsScreen.route,
        icon = R.drawable.ic_tip
    ),
    NavigationDrawerItem(
        name = "Crime Hotspot Areas",
        route = Screen.HotspotsScreen.route,
        icon = R.drawable.ic_danger
    ),
    NavigationDrawerItem(
        name = "Emergency Contacts",
        route = Screen.ContactsScreen.route,
        icon = R.drawable.ic_contact
    )
)

