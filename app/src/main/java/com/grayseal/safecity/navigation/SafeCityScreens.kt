package com.grayseal.safecity.navigation

enum class SafeCityScreens {
    ContactsScreen,
    StatisticsScreen,
    PreventionsScreen,
    HotspotsScreen;

    companion object {
        fun fromRoute(route: String): SafeCityScreens = when (route.substringBefore("/")) {
            ContactsScreen.name -> ContactsScreen
            StatisticsScreen.name -> StatisticsScreen
            PreventionsScreen.name -> PreventionsScreen
            HotspotsScreen.name -> HotspotsScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}