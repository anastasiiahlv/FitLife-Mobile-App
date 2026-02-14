package com.example.fitlife.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun FitLifeBottomBar(navController: NavController) {
    val items = listOf(
        BottomItem(Routes.CENTERS, "Centers", Icons.Filled.List),
        BottomItem(Routes.FAVORITES, "Favorites", Icons.Filled.Favorite),
        BottomItem(Routes.STATS, "Stats", Icons.Filled.ShowChart),
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        // щоб не накопичувати копії екранів у backstack
                        popUpTo(Routes.CENTERS) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
