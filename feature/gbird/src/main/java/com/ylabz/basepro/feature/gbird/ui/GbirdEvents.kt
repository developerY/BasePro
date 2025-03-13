package com.ylabz.basepro.feature.gbird.ui

sealed class GbirdEvent {
    object LoadGbird : GbirdEvent()
    data class UpdateSetting(val settingKey: String, val settingValue: String) : GbirdEvent()
    object DeleteAllEntries : GbirdEvent()  // New event to delete all entries
}

