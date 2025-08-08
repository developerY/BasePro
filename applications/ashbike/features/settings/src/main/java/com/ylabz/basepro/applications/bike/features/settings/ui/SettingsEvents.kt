package com.ylabz.basepro.applications.bike.features.settings.ui

import com.ylabz.basepro.applications.bike.database.ProfileData
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel


enum class ProfileField { NAME, HEIGHT, WEIGHT }


sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    data class UpdateSetting(val key: String, val value: String) : SettingsEvent()
    data class SaveProfile(val profile: ProfileData) : SettingsEvent()
    data class UpdateEnergyLevel(val level: LocationEnergyLevel) : SettingsEvent()
    data class OnShowGpsCountdownChanged(val show: Boolean) : SettingsEvent() // New event
}
