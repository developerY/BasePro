package com.ylabz.basepro.applications.bike.features.trips.ui

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigator
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.mapper.BikeRide
import com.ylabz.basepro.applications.bike.features.trips.data.SyncRideUseCase
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import javax.inject.Named
import androidx.health.connect.client.records.Record
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn


/**
 * So, the data flow is:
 * 1.BikeRideCard (UI) triggers sync.
 * 2.It prepares List<Record> (using a helper like bikeToHealthConnectRecords).
 * 3.It sends HealthEvent.Insert to HealthViewModel.
 * 4.HealthViewModel calls insertExerciseSession with the records.
 * 5.HealthViewModel.insertExerciseSession delegates to HealthSessionManager.insertExerciseSession.
 * 6.HealthSessionManager.insertExerciseSession calls healthConnectClient.insertRecords(records).
 * 7.Updating the isSynced or isHealthDataSynced flag in your local database
 */

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val bikeRideRepo: BikeRideRepo,
    private val syncRideUseCase: SyncRideUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripsUIState>(TripsUIState.Loading)
    val uiState: StateFlow<TripsUIState> = _uiState

    // Unsynced rides count - now directly from the repository
    val unsyncedRidesCount: StateFlow<Int> =
        bikeRideRepo.getUnsyncedRidesCount() // Assumes getUnsyncedRidesCount returns Flow<Int>
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    // session start/end times
    private var startTime = 0L
    private var endTime = 0L

    // track whether we’re currently riding
    var isTracking by mutableStateOf(false)
        private set

    init {
        onEvent(TripsEvent.LoadData)
    }


    fun onEvent(event: TripsEvent) {
        when (event) {
            is TripsEvent.LoadData -> loadData()
            is TripsEvent.UpdateRideNotes -> updateRideNotes(event.itemId, event.notes)
            is TripsEvent.DeleteItem -> deleteItem(event.itemId)
            is TripsEvent.DeleteAll -> deleteAll()
            is TripsEvent.OnRetry -> onEvent(TripsEvent.LoadData)
            is TripsEvent.StopSaveRide -> {
                endTime = System.currentTimeMillis()
                isTracking = false
            }
            is TripsEvent.BuildBikeRec -> { buildHealthConnectRecordsForRide(event.ride) }
        }
    }

    // Function to be called from the UI layer (e.g., TripsUIRoute)
    fun markRideAsSyncedInLocalDb(rideId: String, healthConnectId: String?) {
        viewModelScope.launch {
            try {
                bikeRideRepo.markRideAsSyncedToHealthConnect(rideId, healthConnectId)
                Log.d("TripsViewModel", "Successfully marked ride $rideId as synced in local DB.")
                // Data will refresh automatically due to observing flows from the repo.
                // If not, you might need to trigger a refresh via onEvent(TripsEvent.LoadData)
                // but ideally, the flows handle this.
            } catch (e: Exception) {
                Log.e("TripsViewModel", "Failed to mark ride $rideId as synced in local DB.", e)
                // Optionally, communicate this error back to the UI, e.g., via a SharedFlow for snackbars
                handleError(e) // Using existing error handler
            }
        }
    }

    fun updateRideNotes(rideId: String, notes: String) {
        viewModelScope.launch {
            try {
                bikeRideRepo.updateRideNotes(rideId, notes)
                onEvent(TripsEvent.LoadData) // Keep this to refresh the list if notes change UI
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun buildHealthConnectRecordsForRide(ride: BikeRide): List<Record> {
        return syncRideUseCase(ride)
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                bikeRideRepo.deleteById(itemId)
                onEvent(TripsEvent.LoadData) // Keep this to refresh list
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteAll() {
        viewModelScope.launch {
            try {
                bikeRideRepo.deleteAll()
                onEvent(TripsEvent.LoadData) // Keep this to refresh list
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = TripsUIState.Loading
            try {
                bikeRideRepo
                    .getAllRidesWithLocations() // Flow<List<RideWithLocations>>
                    .collect { ridesWithLocs ->
                        _uiState.value = TripsUIState.Success(ridesWithLocs)
                        // The _unsyncedRidesCount is now handled by its own StateFlow from the repo
                        // No longer need: _unsyncedRidesCount.value = ridesWithLocs.count { !it.bikeRideEnt.isHealthDataSynced }
                    }
            } catch (e: Exception) {
                _uiState.value = TripsUIState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun handleError(e: Exception) {
        _uiState.value = TripsUIState.Error(message = e.localizedMessage ?: "Unknown error")
    }
}
