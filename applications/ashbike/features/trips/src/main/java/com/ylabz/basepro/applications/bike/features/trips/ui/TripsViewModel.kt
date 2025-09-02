package com.ylabz.basepro.applications.bike.features.trips.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.records.Record
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.mapper.toBikeRide
import com.ylabz.basepro.applications.bike.features.trips.domain.MarkRideAsSyncedUseCase
import com.ylabz.basepro.applications.bike.features.trips.domain.SyncRideUseCase
import com.ylabz.basepro.applications.bike.features.trips.ui.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Side-effect definition
sealed interface TripsSideEffect {
    data class RequestHealthConnectSync(val rideId: String, val records: List<Record>) :
        TripsSideEffect
}

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val application: Application, // <-- Inject Application
    private val bikeRideRepo: BikeRideRepo,
    private val syncRideUseCase: SyncRideUseCase,
    private val markRideAsSyncedUseCase: MarkRideAsSyncedUseCase
) : ViewModel() {

    private val _uiState: StateFlow<TripsUIState> =
        bikeRideRepo.getAllRidesWithLocations()
            .map { rides ->
                val uiModels = rides.map {
                    it.toUiModel(
                        resources = application.resources // <-- Use application.resources
                    )
                }
                TripsUIState.Success(uiModels) as TripsUIState
            }
            .catch { e -> emit(TripsUIState.Error(e.localizedMessage ?: "Unknown error")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TripsUIState.Loading
            )
    val uiState: StateFlow<TripsUIState> = _uiState

    // Side-effect channel and flow
    private val _sideEffect = Channel<TripsSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    // track whether we’re currently riding
    var isTracking by mutableStateOf(false)
        private set

    fun onEvent(event: TripsEvent) {
        when (event) {
            is TripsEvent.LoadData -> { /* Data is loaded reactively by the uiState flow */
            }

            is TripsEvent.UpdateRideNotes -> updateRideNotes(event.itemId, event.notes)
            is TripsEvent.DeleteItem -> deleteItem(event.itemId)
            is TripsEvent.DeleteAll -> deleteAll()
            is TripsEvent.OnRetry -> { /* Handled by reactive flow */
            }

            is TripsEvent.StopSaveRide -> {
                isTracking = false
            }

            is TripsEvent.SyncRide -> handleSyncRide(event.rideId)
            is TripsEvent.BuildBikeRec -> { /* This event might be obsolete now */
            }
        }
    }

    private fun handleSyncRide(rideId: String) {
        viewModelScope.launch {
            // 1. Get the full ride data from the repository
            val rideWithLocations = bikeRideRepo.getRideWithLocations(rideId).firstOrNull()
            if (rideWithLocations == null) {
                Log.e("TripsViewModel", "Could not find ride with ID $rideId to sync.")
                return@launch
            }
            val domainRide = rideWithLocations.toBikeRide()

            // 2. Build the Health Connect records
            val records = syncRideUseCase(domainRide)

            // 3. Emit the side effect for the UI to handle
            _sideEffect.send(TripsSideEffect.RequestHealthConnectSync(rideId, records))
        }
    }

    fun markRideAsSyncedInLocalDb(rideId: String, healthConnectId: String?) {
        viewModelScope.launch {
            try {
                markRideAsSyncedUseCase(rideId, healthConnectId)
                Log.d("TripsViewModel", "Successfully marked ride $rideId as synced in local DB.")
            } catch (e: Exception) {
                Log.e("TripsViewModel", "Failed to mark ride $rideId as synced in local DB.", e)
            }
        }
    }

    private fun updateRideNotes(rideId: String, notes: String) {
        viewModelScope.launch { bikeRideRepo.updateRideNotes(rideId, notes) }
    }

    private fun deleteItem(itemId: String) {
        viewModelScope.launch { bikeRideRepo.deleteById(itemId) }
    }

    private fun deleteAll() {
        viewModelScope.launch { bikeRideRepo.deleteAll() }
    }
}
