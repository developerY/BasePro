package com.ylabz.basepro.ashbike.mobile.ui.navigation.graphs

// These are your NavKeys for the NavDisplay system

object BikeHomeKey

object BikeTripsKey

data class BikeSettingsKey(val cardToExpandArg: String? = null)

data class BikeRideDetailKey(val rideId: String)