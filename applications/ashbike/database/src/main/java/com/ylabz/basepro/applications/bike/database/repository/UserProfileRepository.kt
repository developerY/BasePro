package com.ylabz.basepro.applications.bike.database.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ylabz.basepro.applications.bike.database.ProfileData
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// 1) Repository interface (contract)
interface UserProfileRepository {
    val nameFlow: Flow<String>
    val heightFlow: Flow<String>
    val weightFlow: Flow<String>
    val profileReviewedOrSavedFlow: Flow<Boolean>

    /* --- New additions for Location Energy Level ---
    val locationEnergyLevelFlow: Flow<LocationEnergyLevel>
    suspend fun setLocationEnergyLevel(level: LocationEnergyLevel)
    // --- End of new additions --- */

    /* --- New additions for GPS Countdown Timer ---
    val showGpsCountdownFlow: Flow<Boolean>
    suspend fun setShowGpsCountdown(show: Boolean)
    // --- End of new additions --- */

    suspend fun setName(newName: String)
    suspend fun setHeight(cm: String)
    suspend fun setWeight(kg: String)
    suspend fun saveProfile(profile: ProfileData)
}
