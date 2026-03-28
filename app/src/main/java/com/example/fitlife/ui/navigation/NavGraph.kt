package com.example.fitlife.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import com.example.fitlife.ui.screens.centers.CentersScreen
import com.example.fitlife.ui.screens.details.CenterDetailsScreen
import com.example.fitlife.ui.screens.favorites.FavoritesScreen
import com.example.fitlife.ui.screens.stats.StatsScreen

@Composable
fun FitLifeNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.CENTERS,
        modifier = modifier
    ) {
        composable(Routes.CENTERS) {
            CentersScreen(
                onOpenDetails = { centerId ->
                    navController.navigate(Routes.details(centerId))
                }
            )
        }

        composable(
            route = Routes.DETAILS_WITH_ARG,
            arguments = listOf(
                navArgument(Routes.ARG_CENTER_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val centerId = backStackEntry.arguments?.getString(Routes.ARG_CENTER_ID) ?: ""
            CenterDetailsScreen(
                centerId = centerId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                onOpenDetails = { centerId ->
                    navController.navigate(Routes.details(centerId))
                }
            )
        }

        composable(Routes.STATS) {
            StatsScreen()
        }

        composable("details/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            CenterDetailsScreen(
                centerId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
