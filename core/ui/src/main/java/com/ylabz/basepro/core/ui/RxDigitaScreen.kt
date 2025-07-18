package com.ylabz.basepro.core.ui

import kotlinx.serialization.Serializable

const val RXDIGITA = "rxdigita"  // if you plan to use a nested nav graph, otherwise remove it

sealed class RxDigitaScreen(val route: String) {
    object HomeRxDigitaScreen     : RxDigitaScreen("home_bike_screen")
    object TripRxDigitaScreen     : RxDigitaScreen("trip_bike_screen")
    object SettingsRxDigitaScreen : RxDigitaScreen("settings_bike_screen")

    // Detail screen lives at the top level now
    object RideDetailScreen : RxDigitaScreen("ride/{rideId}") {
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