package com.ylabz.basepro.applications.bike.features.trips.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.BikeProEntity
import com.ylabz.basepro.applications.bike.database.BikeProRepo
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.ylabz.basepro.applications.bike.database.mapper.BikePro
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart


@HiltViewModel
class TripsViewModel @Inject constructor(
    private val bikeRideRepo: BikeRideRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripsUIState>(TripsUIState.Loading)
    val uiState: StateFlow<TripsUIState> = _uiState

    private val _selectedItem = MutableStateFlow<BikeProEntity?>(null)
    val selectedItem: StateFlow<BikeProEntity?> = _selectedItem

    init {
        onEvent(TripsEvent.LoadData)
    }

    fun onEvent(event: TripsEvent) {
        when (event) {
            is TripsEvent.LoadData -> loadData()
            //is TripsEvent.DeleteItem -> deleteItem(event.itemId)
            is TripsEvent.DeleteAll -> deleteAll()
            is TripsEvent.OnRetry -> onEvent(TripsEvent.LoadData)
            //is TripsEvent.OnItemClicked -> selectItem(event.itemId)
            is TripsEvent.AddBikeRide -> addBikeRide()
        }
    }

    /*fun selectItem(itemId: Int) {
        viewModelScope.launch {
            // Fetch the item details only if they are not already loaded
            if (_selectedItem.value?.id != itemId) {

                _selectedItem.value = bikeRideRepo.get(itemId)
            }
        }
    }*/

    private fun deleteAll() {
        viewModelScope.launch {
            try {
                bikeRideRepo.deleteAll()
                // Optionally refresh data after deletion
                onEvent(TripsEvent.LoadData)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = TripsUIState.Loading
            try {
                bikeRideRepo.getAllRides().collect { rides ->
                    _uiState.value = TripsUIState.Success(
                        bikeRides = rides
                    )
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }


    private fun addBikeRide() {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()

                val newRide = BikeRideEntity(
                    // Core BikeRide Information
                    startTime        = now,
                    endTime          = now + 30 * 60_000L,  // 30‑minute ride
                    totalDistance    = 10_000f,             // 10 km
                    averageSpeed     = 5.6f,                // ≈20 km/h
                    maxSpeed         = 8.3f,                // ≈30 km/h

                    // Elevation and Fitness Data
                    elevationGain    = 120f,                // meters climbed
                    elevationLoss    = 115f,                // meters descended
                    caloriesBurned   = 450,                 // kcal

                    // Optional Health Connect
                    avgHeartRate          = 125,            // bpm
                    maxHeartRate          = 158,            // bpm
                    healthConnectRecordId = null,
                    isHealthDataSynced    = false,

                    // Environmental & Context
                    weatherCondition = "Sunny, 22°C",
                    rideType         = "Road",

                    // User Feedback
                    notes   = "Evening loop around the park",
                    rating  = 4,     // out of 5
                    isSynced = false,

                    // Bike & Battery
                    bikeId       = "RoadBike‑123",
                    batteryStart = 100,  // %
                    batteryEnd   = 95,   // %

                    // Start/End coords for quick queries
                    startLat = 37.7749,
                    startLng = -122.4194,
                    endLat   = 37.7793,
                    endLng   = -122.4188
                )

                bikeRideRepo.insert(newRide)
                onEvent(TripsEvent.LoadData)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    // Centralized error handling
    private fun handleError(e: Exception) {
        _uiState.value = TripsUIState.Error(message = e.localizedMessage ?: "Unknown error")
    }
}
