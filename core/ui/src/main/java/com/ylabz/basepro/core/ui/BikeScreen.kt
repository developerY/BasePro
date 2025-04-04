package com.ylabz.basepro.core.ui

import kotlinx.serialization.Serializable

const val BIKE = "bike"

sealed class BikeScreen(val route:String ){
    data object HomeBikeScreen : BikeScreen("home_bike_screen")
    data object TripBikeScreen : BikeScreen("trip_bike_screen")
    data object SettingsBikeScreen: BikeScreen("settings_bike_screen")
}