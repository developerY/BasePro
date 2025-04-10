package com.ylabz.basepro.applications.bike.features.settings.ui

sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    data class UpdateSetting(val settingKey: String, val settingValue: String) : SettingsEvent()
    object DeleteAllEntries : SettingsEvent()  // New event to delete all entries
}

