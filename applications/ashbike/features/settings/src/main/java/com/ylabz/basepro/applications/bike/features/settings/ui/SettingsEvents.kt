package com.ylabz.basepro.applications.bike.features.settings.ui

import com.ylabz.basepro.applications.bike.database.ProfileData


enum class ProfileField { NAME, HEIGHT, WEIGHT }


sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    data class UpdateSetting(val key: String, val value: String) : SettingsEvent()
    data class SaveProfile(val profile: ProfileData) : SettingsEvent()
}
