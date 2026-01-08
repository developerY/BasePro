package com.ylabz.basepro.ashbike.wear.presentation.screens.history

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.BikeRideDao
import com.ylabz.basepro.applications.bike.database.mapper.toBikeRide
import com.ylabz.basepro.core.model.bike.BikeRide
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class RideHistoryViewModel @Inject constructor(
    val bikeDao: BikeRideDao
) : ViewModel() {

    val historyList: StateFlow<List<RideHistoryUiItem>> = bikeDao.getAllRidesWithLocations()
        .map { dbList ->
            dbList.map { it.toBikeRide() } // 1. Convert DB -> Domain (BikeRide)
        }
        .map { domainList ->
            domainList.map { bikeRide ->    // 2. Convert Domain -> UI (Lightweight)
                bikeRide.toUiModel()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. NEW: Get Single Ride (for Details)
    fun getRide(rideId: String): Flow<BikeRide?> {
        return bikeDao.getRideWithLocations(rideId).map { it?.toBikeRide() }
    }

    // 3. NEW: Delete Ride
    fun deleteRide(rideId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bikeDao.deleteRide(rideId)
        }
    }

    // Convert YOUR Domain Model to the simple strings needed for the UI
    private fun BikeRide.toUiModel(): RideHistoryUiItem {
        val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

        // Calculate duration logic if not stored explicitly
        val durationMillis = this.endTime - this.startTime
        val durationMin = durationMillis / 1000 / 60

        return RideHistoryUiItem(
            id = this.rideId,
            dateStr = dateFormat.format(Date(this.startTime)),
            // Domain model already has TotalDistance as Float (assuming km based on mapper)
            distanceStr = String.format("%.2f km", this.totalDistance),
            durationStr = "$durationMin min",
            caloriesStr = "${this.caloriesBurned} kcal"
        )
    }
}