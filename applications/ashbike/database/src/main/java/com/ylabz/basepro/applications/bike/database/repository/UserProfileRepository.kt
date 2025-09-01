package com.ylabz.basepro.applications.bike.database.repository

import com.ylabz.basepro.applications.bike.database.ProfileData
import kotlinx.coroutines.flow.Flow

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
