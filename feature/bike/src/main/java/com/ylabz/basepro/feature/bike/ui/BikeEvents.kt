package com.ylabz.basepro.feature.bike.ui

sealed class BikeEvent {
    object LoadBike : BikeEvent()
    data class UpdateSetting(val settingKey: String, val settingValue: String) : BikeEvent()
    object DeleteAllEntries : BikeEvent()  // New event to delete all entries
}

