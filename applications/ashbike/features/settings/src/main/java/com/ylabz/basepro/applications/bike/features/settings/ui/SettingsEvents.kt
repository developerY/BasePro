package com.ylabz.basepro.applications.bike.features.settings.ui


enum class ProfileField { NAME, HEIGHT, WEIGHT }


sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    data class UpdateSetting(val key: String, val value: String) : SettingsEvent()
    data class UpdateProfile(val field: ProfileField, val value: String) : SettingsEvent()
}

