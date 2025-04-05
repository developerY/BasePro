package com.ylabz.basepro.core.ui

import kotlinx.serialization.Serializable

const val BIKE = "bike"  // if you plan to use a nested nav graph, otherwise remove it

sealed class BikeScreen(val route: String) {
    object HomeBikeScreen : BikeScreen("home_bike_screen")
    object TripBikeScreen : BikeScreen("trip_bike_screen")
    object SettingsBikeScreen : BikeScreen("settings_bike_screen")
}

// use data if you need to pass values
/*
sealed class Screen(val route: String) {
    object Home : Screen("home")
    data class Detail(val itemId: String) : Screen("detail/$itemId")
}
*/