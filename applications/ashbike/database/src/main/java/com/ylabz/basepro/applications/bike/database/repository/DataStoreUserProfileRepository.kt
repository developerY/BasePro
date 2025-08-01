package com.ylabz.basepro.applications.bike.database.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey // Added import
import androidx.datastore.preferences.preferencesDataStore
import com.ylabz.basepro.applications.bike.database.ProfileData
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private object UserPrefsDefaults {
    const val NAME_DEFAULT   = "Ash Monster"
    const val HEIGHT_DEFAULT = "171"  // cm
    const val WEIGHT_DEFAULT = "72"   // kg
    const val PROFILE_REVIEWED_OR_SAVED_DEFAULT = false // Default for the new flag
    // --- New default for Location Energy Level ---
    val LOCATION_ENERGY_LEVEL_DEFAULT = LocationEnergyLevel.BALANCED.ordinal
}

// Keys for your preferences
private object UserPrefsKeys {
    val NAME   = stringPreferencesKey("user_name")
    val HEIGHT = stringPreferencesKey("user_height_cm")
    val WEIGHT = stringPreferencesKey("user_weight_kg")
    // Add the new boolean key
    val PROFILE_REVIEWED_OR_SAVED = booleanPreferencesKey("profile_reviewed_or_saved_by_user")
    // --- New key for Location Energy Level (stores the ordinal as an Int) ---
    val LOCATION_ENERGY_LEVEL = intPreferencesKey("location_energy_level")
}

// 2) DataStore implementation
// 2) DataStore‐backed implementation
@Singleton
class DataStoreUserProfileRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserProfileRepository {

    override val nameFlow: Flow<String> = dataStore.data
        .map { prefs ->
            prefs[UserPrefsKeys.NAME].takeUnless { it.isNullOrBlank() }
                ?: UserPrefsDefaults.NAME_DEFAULT
        }

    override val heightFlow: Flow<String> = dataStore.data
        .map { prefs ->
            prefs[UserPrefsKeys.HEIGHT].takeUnless { it.isNullOrBlank() }
                ?: UserPrefsDefaults.HEIGHT_DEFAULT
        }

    override val weightFlow: Flow<String> = dataStore.data
        .map { prefs ->
            prefs[UserPrefsKeys.WEIGHT].takeUnless { it.isNullOrBlank() }
                ?: UserPrefsDefaults.WEIGHT_DEFAULT
        }

    // Implement the new flow from the interface
    override val profileReviewedOrSavedFlow: Flow<Boolean> = dataStore.data
        .map { prefs ->
            prefs[UserPrefsKeys.PROFILE_REVIEWED_OR_SAVED] ?: UserPrefsDefaults.PROFILE_REVIEWED_OR_SAVED_DEFAULT
        }

    // --- Implementation for Location Energy Level ---
    override val locationEnergyLevelFlow: Flow<LocationEnergyLevel> = dataStore.data
        .map { prefs ->
            val ordinal = prefs[UserPrefsKeys.LOCATION_ENERGY_LEVEL] ?: UserPrefsDefaults.LOCATION_ENERGY_LEVEL_DEFAULT
            LocationEnergyLevel.values()[ordinal]
        }

    override suspend fun setLocationEnergyLevel(level: LocationEnergyLevel) {
        dataStore.edit { prefs ->
            prefs[UserPrefsKeys.LOCATION_ENERGY_LEVEL] = level.ordinal
        }
    }
    // --- End of implementation for Location Energy Level ---

    override suspend fun setName(newName: String) {
        dataStore.edit { prefs -> prefs[UserPrefsKeys.NAME] = newName }
    }

    override suspend fun setHeight(cm: String) {
        dataStore.edit { prefs -> prefs[UserPrefsKeys.HEIGHT] = cm }
    }

    override suspend fun setWeight(kg: String) {
        dataStore.edit { prefs -> prefs[UserPrefsKeys.WEIGHT] = kg }
    }

    override suspend fun saveProfile(profile: ProfileData) {
        dataStore.edit { prefs ->
            prefs[UserPrefsKeys.NAME] = profile.name
            prefs[UserPrefsKeys.HEIGHT] = profile.heightCm
            prefs[UserPrefsKeys.WEIGHT] = profile.weightKg
            // Set the reviewed/saved flag to true when profile is saved
            prefs[UserPrefsKeys.PROFILE_REVIEWED_OR_SAVED] = true
        }
    }
}
