package com.ylabz.basepro.core.ui

sealed class BikeScreen(val route: String) {
    object HomeBikeScreen : BikeScreen("home_bike_screen")
    object TripBikeScreen : BikeScreen("trip_bike_screen")

    object SettingsBikeScreen : BikeScreen("settings_bike_screen") {
        const val ARG_CARD_TO_EXPAND = "cardToExpandArg" // Argument key as used in nav graph

        // Helper to build a concrete path for settings with optional argument
        fun createRoute(cardToExpand: String? = null): String {
            return if (cardToExpand != null) {
                "$route?$ARG_CARD_TO_EXPAND=$cardToExpand"
            } else {
                route
            }
        }
    }

    // Detail screen lives at the top level now
    object RideDetailScreen : BikeScreen("ride/{rideId}") {
        // Helper to build a concrete path
        fun createRoute(rideId: String) = "ride/$rideId"
    }
}


// use data if you need to pass values
/*
sealed class Screen(val route: String) {
    object Home : Screen("home")
    data class Detail(val itemId: String) : Screen("detail/$itemId")
}
*/