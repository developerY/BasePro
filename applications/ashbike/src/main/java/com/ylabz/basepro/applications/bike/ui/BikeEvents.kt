package com.ylabz.basepro.applications.bike.ui

sealed class BikeEvent {
    object LoadBike : BikeEvent()
    object Connect : BikeEvent()
    // object Disconnect : BikeEvent()
    data class UpdateSetting(val settingKey: String, val settingValue: String) : BikeEvent()
    object DeleteAllEntries : BikeEvent()  // New event to delete all entries

    object StartPauseRide  : BikeEvent()
    object StopSaveRide        : BikeEvent()   // now both Stop & Save
}

