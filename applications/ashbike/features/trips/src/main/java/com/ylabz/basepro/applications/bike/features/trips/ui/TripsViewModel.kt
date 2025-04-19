package com.ylabz.basepro.applications.bike.features.trips.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.BikeProEntity
import com.ylabz.basepro.applications.bike.database.BikeProRepo
import com.ylabz.basepro.applications.bike.database.RideEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.ylabz.basepro.applications.bike.database.mapper.BikePro


@HiltViewModel
class TripsViewModel @Inject constructor(
    private val repository: BikeProRepo,
    piravte val repository:
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
            is TripsEvent.AddItem -> addItem(event.name)
            is TripsEvent.DeleteItem -> deleteItem(event.itemId)
            is TripsEvent.DeleteAll -> deleteAll()
            is TripsEvent.OnRetry -> onEvent(TripsEvent.LoadData)
            is TripsEvent.OnItemClicked -> selectItem(event.itemId)
        }
    }

    fun selectItem(itemId: Int) {
        viewModelScope.launch {
            // Fetch the item details only if they are not already loaded
            if (_selectedItem.value?.id != itemId) {

                _selectedItem.value = repository.getBikeProById(itemId)
            }
        }
    }

    private fun deleteAll() {
        viewModelScope.launch {
            try {
                repository.deleteAll()
                // Optionally refresh data after deletion
                onEvent(TripsEvent.LoadData)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = TripsUIState.Loading
                repository.allGetBikePros().collect { data ->
                    _uiState.value = TripsUIState.Success(data = data)
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }


    /** Called by your “Add” button. */
    fun addRide() {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()

                // 1) Build a new RideEntity. Omitting rideId uses the default UUID.
                val newRide = RideEntity(
                    startTimestamp = now,
                    endTimestamp = now + 5 * 60_000L,   // e.g. +5 minutes
                    distanceMeters = 0.0,                // start at zero
                    avgSpeedMetersPerSec = 0.0,
                    maxSpeedMetersPerSec = 0.0,
                    calories = 0.0,                // or null
                    elevationGainMeters = 0.0                 // or null
                    // …any other nullable fields will use their defaults
                )

                // 2) (Optional) If you want to seed it with one sample location:
                // val sampleLocation = RideLocationEntity(
                //     rideOwnerId = newRide.rideId,
                //     timestamp   = now,
                //     latitude    = 0.0,
                //     longitude   = 0.0
                // )
                // rideRepository.saveRide(newRide, listOf(sampleLocation))

                // 3) Save it (no locations for now)
                rideRepository.saveRide(newRide)

                // 4) Trigger a reload of your UI list
                onEvent(TripsEvent.LoadData)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun addItem(name: String?) {
        viewModelScope.launch {
            try {
                // Create a new BikePro entity.
                // Adjust the default values for timestamps, speeds, distance, etc., as required.
                val newBikePro = BikePro(
                    // Assuming the id is auto-generated; omit it or rely on default value.
                    // title = name,
                    startTime = System.currentTimeMillis(),          // Current time as start
                    endTime = System.currentTimeMillis() + 5 * 60 * 1000, // Example: ride lasts 5 minutes
                    totalDistance = 0f,                                // Starting with zero distance
                    averageSpeed = 0f,
                    maxSpeed = 0f,
                    elevationGain = 0f,
                    elevationLoss = 0f,
                    caloriesBurned = 0,
                    // Optional Health Connect fields left as defaults:
                    avgHeartRate = null,
                    maxHeartRate = null,
                    healthConnectRecordId = null,
                    isHealthDataSynced = false,
                    // Location and route data (placeholder values)
                    startLat = 0.0,
                    startLng = 0.0,
                    endLat = 0.0,
                    endLng = 0.0,
                    routeJson = null,
                    // Additional optional metadata
                    weatherCondition = null,
                    rideType = null,
                    notes = null,
                    rating = null,
                    isSynced = false,
                    bikeId = null,
                    batteryStart = null,
                    batteryEnd = null
                )

                repository.insert(newBikePro)
                // Refresh data after adding the new item.
                onEvent(TripsEvent.LoadData)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }


    private fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteById(itemId)
                onEvent(TripsEvent.LoadData)  // Refresh data after deletion
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
