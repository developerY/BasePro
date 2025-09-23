package com.ylabz.basepro.applications.photodo.features.settings.ui


enum class ProfileField { NAME, HEIGHT, WEIGHT }


sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    data class UpdateSetting(val key: String, val value: String) : SettingsEvent()
}
