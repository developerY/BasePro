package com.ylabz.basepro.feature.heatlh.ui

// ... other imports ...
// BikeRideRepository import is removed
import android.content.Context
import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.permission.HealthPermission.Companion.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.service.health.HealthSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

// Updated HealthSideEffect
sealed interface HealthSideEffect {
    data class LaunchPermissions(val permissions: Set<String>) : HealthSideEffect
    data class BikeRideSyncedToHealth(val rideId: String, val healthConnectId: String) :
        HealthSideEffect // Added this
}

@HiltViewModel
class HealthViewModel @Inject constructor(
    @ApplicationContext private val appCtx: Context,
    val healthSessionManager: HealthSessionManager
    // bikeRideRepository injection is removed
) : ViewModel() {

    private val _uiState = MutableStateFlow<HealthUiState>(HealthUiState.Uninitialized)
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HealthSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    val permissions = setOf(
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(DistanceRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(WeightRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class)
    )

    val backgroundReadPermissions = setOf(PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND)

    var backgroundReadAvailable = mutableStateOf(false)
        private set
    var backgroundReadGranted = mutableStateOf(false)

    val permissionsLauncher = healthSessionManager.requestPermissionsActivityContract()

    val syncedIds: StateFlow<Set<String>> = uiState
        .map { state ->
            (state as? HealthUiState.Success)
                ?.healthData
                ?.mapNotNull { it.metadata.clientRecordId }
                ?.toSet()
                .orEmpty()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptySet()
        )

    init {
        // ViewModel waits for UI to trigger first data load or permission request.
    }


    fun onEvent(event: HealthEvent) {
        when (event) {
            is HealthEvent.LoadHealthData,
            is HealthEvent.Retry -> initialLoad()

            is HealthEvent.RequestPermissions -> requestPermissionsOnClick()
            is HealthEvent.Insert -> insertBikeRideSessionAndEmitEffect(event) // Renamed method
            is HealthEvent.DeleteAll -> delData()
            is HealthEvent.ReadAll -> readAllDAta()
        }
    }

    // In HealthViewModel.kt
    private fun requestPermissionsOnClick() {
        viewModelScope.launch {
            val hasPermissions =
                healthSessionManager.hasAllPermissions(permissions) // Store the result
            Log.d(
                "HealthViewModel",
                "RequestPermissions: healthSessionManager.hasAllPermissions returned: $hasPermissions"
            ) // Log the result
            if (!hasPermissions) {
                Log.d(
                    "HealthViewModel",
                    "RequestPermissions: Permissions NOT granted. Emitting LaunchPermissions side effect."
                ) // Log before emitting
                _sideEffect.emit(HealthSideEffect.LaunchPermissions(permissions))
            } else {
                Log.d(
                    "HealthViewModel",
                    "RequestPermissions: Permissions ARE already granted. Setting UI to Success."
                ) // Log if already granted
                // _uiState.value = HealthUiState.Success(readSessionInputs()) // This line might need to be careful if readSessionInputs() is heavy or has side effects itself before UI is ready
                initialLoad() // Calling initialLoad() here might be better as it handles setting Loading then Success/Error/PermissionsRequired
            }
        }
    }

    // Renamed and updated method
    private fun insertBikeRideSessionAndEmitEffect(event: HealthEvent.Insert) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                Log.d(
                    "HealthViewModel",
                    "Attempting to sync rideId: ${event.rideId} with records: ${event.records.map { it::class.simpleName }} to Health Connect."
                )
                try {
                    val clientRideId = healthSessionManager.insertBikeRideWithAssociatedData(
                        event.rideId,
                        event.records
                    )
                    Log.d(
                        "HealthViewModel",
                        "Successfully synced ride $clientRideId to Health Connect."
                    )

                    // Emit a side effect instead of calling repository directly
                    _sideEffect.emit(
                        HealthSideEffect.BikeRideSyncedToHealth(
                            rideId = clientRideId,
                            healthConnectId = clientRideId
                        )
                    )
                    Log.d(
                        "HealthViewModel",
                        "Emitted BikeRideSyncedToHealth side effect for ride $clientRideId."
                    )

                    initialLoad() // Refresh health data list from Health Connect (optional, consider if needed immediately)

                } catch (hcException: Exception) { // Catching exceptions from Health Connect operation
                    Log.e(
                        "HealthViewModel",
                        "Error during Health Connect sync for rideId ${event.rideId}",
                        hcException
                    )
                    _uiState.value =
                        HealthUiState.Error("Sync failed for ride ${event.rideId}: ${hcException.message}")
                    // Do not emit side effect or call initialLoad() on HC error
                }
            }
        }
    }

    private fun initialLoad() {
        _uiState.value = HealthUiState.Loading
        viewModelScope.launch {
            if (healthSessionManager.availability.value != HealthConnectClient.SDK_AVAILABLE) {
                _uiState.value = HealthUiState.Error("Health Connect SDK not available.")
                return@launch
            }
            try {
                if (healthSessionManager.hasAllPermissions(permissions)) {
                    val sessions = readSessionInputs()
                    _uiState.value = HealthUiState.Success(sessions)
                    observeHealthConnectChanges() // Start observing only after successful load with permissions
                } else {
                    Log.d("HealthViewModel", "Permissions required for initial load.")
                    _uiState.value =
                        HealthUiState.PermissionsRequired("Displaying Health Connect data requires permissions.")
                }
            } catch (e: Exception) {
                Log.e("HealthViewModel", "Failed to perform initial load", e)
                _uiState.value = HealthUiState.Error("Failed to read health data: ${e.message}")
            }
        }
    }

    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        if (healthSessionManager.availability.value != HealthConnectClient.SDK_AVAILABLE) {
            _uiState.value =
                HealthUiState.Error("Health Connect SDK not available. Cannot perform action.")
            return
        }
        if (healthSessionManager.hasAllPermissions(permissions)) {
            try {
                block()
            } catch (e: RemoteException) {
                Log.e("HealthViewModel", "Action failed due to Health Connect service error.", e)
                _uiState.value =
                    HealthUiState.Error("Health Connect communication error: ${e.message}")
            } catch (e: IOException) {
                Log.e("HealthViewModel", "Action failed due to I/O error.", e)
                _uiState.value = HealthUiState.Error("Data operation failed: ${e.message}")
            } catch (e: Exception) {
                Log.e("HealthViewModel", "Action failed with an unexpected error.", e)
                _uiState.value = HealthUiState.Error("Action failed: ${e.message}")
            }
        } else {
            Log.d(
                "HealthViewModel",
                "Permissions check failed. Emitting side effect to request permissions."
            )
            _sideEffect.emit(HealthSideEffect.LaunchPermissions(permissions))
            _uiState.value =
                HealthUiState.PermissionsRequired("Action requires Health Connect permissions.")
        }
    }

    private var isObservingChanges = false
    private fun observeHealthConnectChanges() {
        if (isObservingChanges || healthSessionManager.availability.value != HealthConnectClient.SDK_AVAILABLE) return
        viewModelScope.launch {
            isObservingChanges = true
            try {
                val token =
                    healthSessionManager.getChangesToken(setOf(ExerciseSessionRecord::class))
                healthSessionManager.getChanges(token)
                    .catch { e ->
                        Log.e("HealthViewModel", "Error in Health Connect changes flow", e)
                        isObservingChanges = false
                        _uiState.value =
                            HealthUiState.Error("Error observing Health Connect changes: ${e.message}")
                    }
                    .filterIsInstance<HealthSessionManager.ChangesMessage.ChangeList>()
                    .collect {
                        Log.d(
                            "HealthViewModel",
                            "Detected changes from Health Connect. Reloading data."
                        )
                        initialLoad()
                    }
            } catch (e: Exception) {
                Log.e("HealthViewModel", "Failed to start observing Health Connect changes", e)
                isObservingChanges = false
                _uiState.value =
                    HealthUiState.Error("Could not set up Health Connect change listener: ${e.message}")
            }
        }
    }

    private fun readAllDAta() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthSessionManager.logAllHealthData()
            }
        }
    }

    private suspend fun readSessionInputs(): List<ExerciseSessionRecord> {
        val sevenDaysAgo = ZonedDateTime.now().minusDays(7).truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()
        Log.d("HealthViewModel", "Reading exercise sessions from $sevenDaysAgo to $now")
        return healthSessionManager.readExerciseSessions(sevenDaysAgo.toInstant(), now)
    }

    private fun delData() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthSessionManager.deleteAllSessionData()
                initialLoad()
            }
        }
    }

}
