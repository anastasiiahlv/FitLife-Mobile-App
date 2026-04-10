package com.example.fitlife.ui.navigation

object Routes {
    const val CENTERS = "centers"
    const val FAVORITES = "favorites"
    const val STATS = "stats"
    const val SETTINGS = "settings"

    const val DETAILS = "details"
    const val DETAILS_WITH_ARG = "details/{centerId}"

    const val ARG_CENTER_ID = "centerId"

    fun details(centerId: String): String = "$DETAILS/$centerId"
}
