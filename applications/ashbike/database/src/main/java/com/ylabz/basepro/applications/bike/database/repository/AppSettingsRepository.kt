package com.ylabz.basepro.applications.bike.database.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// 2a) Define the keys you need
private object SettingsPrefsKeys {
    val THEME         = stringPreferencesKey("settings_theme")
    val LANGUAGE      = stringPreferencesKey("settings_language")
    val NOTIFICATIONS = stringPreferencesKey("settings_notifications")
    val UNITS         = stringPreferencesKey("settings_units") // Added Units Key
    val GPS_ACCURACY  = stringPreferencesKey("settings_gps_accuracy")
}

// 2b) Repository contract – your ViewModel only talks to this
interface AppSettingsRepository {
    val themeFlow: Flow<String>
    val languageFlow: Flow<String>
    val notificationsFlow: Flow<String>
    val unitsFlow: Flow<String> // Added Units Flow
    val gpsAccuracyFlow: Flow<String>

    suspend fun setTheme(theme: String)
    suspend fun setLanguage(language: String)
    suspend fun setNotifications(option: String)
    suspend fun setUnits(units: String) // Added setUnits function
    suspend fun setGpsAccuracy(accuracy: String)
}

// 2c) DataStore-backed impl
@Singleton
class DataStoreAppSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppSettingsRepository {

    override val themeFlow: Flow<String> = dataStore.data
        .map { it[SettingsPrefsKeys.THEME] ?: "System" }

    override val languageFlow: Flow<String> = dataStore.data
        .map { it[SettingsPrefsKeys.LANGUAGE] ?: "English" }

    override val notificationsFlow: Flow<String> = dataStore.data
        .map { it[SettingsPrefsKeys.NOTIFICATIONS] ?: "Enabled" }

    override val unitsFlow: Flow<String> = dataStore.data // Added Units Flow implementation
        .map { it[SettingsPrefsKeys.UNITS] ?: "Metric (SI)" } // Defaulting to Metric

    override val gpsAccuracyFlow: Flow<String> = dataStore.data
        .map { it[SettingsPrefsKeys.GPS_ACCURACY] ?: "Balanced" }

    override suspend fun setTheme(theme: String) {
        dataStore.edit { prefs -> prefs[SettingsPrefsKeys.THEME] = theme }
    }
    override suspend fun setLanguage(language: String) {
        dataStore.edit { prefs -> prefs[SettingsPrefsKeys.LANGUAGE] = language }
    }
    override suspend fun setNotifications(option: String) {
        dataStore.edit { prefs -> prefs[SettingsPrefsKeys.NOTIFICATIONS] = option }
    }
    override suspend fun setUnits(units: String) { // Added setUnits implementation
        dataStore.edit { prefs -> prefs[SettingsPrefsKeys.UNITS] = units }
    }
    override suspend fun setGpsAccuracy(accuracy: String) {
        dataStore.edit { prefs -> prefs[SettingsPrefsKeys.GPS_ACCURACY] = accuracy }
    }
}
