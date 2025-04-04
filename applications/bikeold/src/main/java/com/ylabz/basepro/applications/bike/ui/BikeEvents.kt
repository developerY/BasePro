package com.ylabz.basepro.applications.bike.ui

sealed class BikeEvent {
    object LoadBike : BikeEvent()
    object Connect : BikeEvent()
    // object Disconnect : BikeEvent()
    // object StartRide : BikeEvent()
    // object StopRide : BikeEvent()
    data class UpdateSetting(val settingKey: String, val settingValue: String) : BikeEvent()
    object DeleteAllEntries : BikeEvent()  // New event to delete all entries
}

