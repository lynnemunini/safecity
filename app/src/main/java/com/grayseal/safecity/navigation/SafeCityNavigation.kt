package com.grayseal.safecity.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grayseal.safecity.screens.emergency.ContactsScreen
import com.grayseal.safecity.screens.hotspots.HotspotsScreen
import com.grayseal.safecity.screens.main.MainScreen
import com.grayseal.safecity.screens.report.ReportScreen
import com.grayseal.safecity.screens.statistics.ChartsScreen
import com.grayseal.safecity.screens.statistics.StatisticsScreen

@Composable
fun SafeCityNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController)
        }
        composable(
            route = Screen.ReportScreen.route + "/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            ReportScreen(navController = navController, name = entry.arguments?.getString("name"))
        }
        composable(route = Screen.StatisticsScreen.route) {
            StatisticsScreen(navController = navController)
        }
        composable(
            route = Screen.ChartsScreen.route + "/{id}",
            arguments = listOf(navArgument(name = "id") {
                type = NavType.StringType
            })
        ) { navBack ->
            navBack.arguments?.getString("id").let { id ->
                ChartsScreen(navController = navController, id = id!!)
            }
        }
        composable(route = Screen.HotspotsScreen.route) {
            HotspotsScreen(navController = navController)
        }
        composable(route = Screen.ContactsScreen.route) {
            ContactsScreen(navController = navController)
        }
    }
}
