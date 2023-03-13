package com.grayseal.safecity.navigation

sealed class Screen(val route: String){
    object MainScreen : Screen("main_screen")
    object ReportScreen : Screen("report_screen")
    object ContactsScreen: Screen("contact_screen")
    object StatisticsScreen: Screen("statistics_screen")
    object PreventionsScreen: Screen("preventions_screen")
    object HotspotsScreen: Screen("hotspots_screen")

    fun withArgs(vararg args: String): String{
        return buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }
}
