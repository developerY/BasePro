package com.ylabz.basepro.feature.heatlh.ui

import android.app.Application
import android.content.Context
import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectFeatures
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
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random


sealed interface HealthSideEffect {
    data class LaunchPermissions(val permissions: Set<String>) : HealthSideEffect
}

@HiltViewModel
class HealthViewModel @Inject constructor(
    @ApplicationContext private val appCtx: Context,
    val healthSessionManager: HealthSessionManager,
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
        // The ViewModel now waits for the UI to trigger the first data load.
    }

    fun onEvent(event: HealthEvent) {
        when (event) {
            is HealthEvent.LoadHealthData,
            is HealthEvent.Retry -> initialLoad()
            is HealthEvent.RequestPermissions -> requestPermissionsOnClick()
            is HealthEvent.Insert -> insertExerciseSession(event)
            is HealthEvent.DeleteAll -> delData()
            //... keep other events if needed
            is HealthEvent.ReadAll ->  readAllDAta()
        }
    }

    private fun requestPermissionsOnClick() {
        viewModelScope.launch {
            if (!healthSessionManager.hasAllPermissions(permissions)) {
                _sideEffect.emit(HealthSideEffect.LaunchPermissions(permissions))
            }
        }
    }

    private fun insertExerciseSession(event: HealthEvent.Insert) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthSessionManager.insertRecords(event.records)
                initialLoad() // Refresh data after a successful sync
            }
        }
    }

    private fun initialLoad() {
        _uiState.value = HealthUiState.Loading
        viewModelScope.launch {
            if (healthSessionManager.availability.value != HealthConnectClient.SDK_AVAILABLE) {
                _uiState.value = HealthUiState.Error("Health Connect is not available.")
                return@launch
            }
            try {
                if (healthSessionManager.hasAllPermissions(permissions)) {
                    val sessions = readSessionInputs()
                    _uiState.value = HealthUiState.Success(sessions)
                    observeHealthConnectChanges()
                } else {
                    _uiState.value = HealthUiState.PermissionsRequired("Permissions needed")
                }
            } catch (e: Exception) {
                _uiState.value = HealthUiState.Error("Failed to read permissions: ${e.message}")
            }
        }
    }

    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        if (healthSessionManager.hasAllPermissions(permissions)) {
            try {
                block()
            } catch (e: Exception) {
                _uiState.value = HealthUiState.Error("Action failed: ${e.message}")
            }
        } else {
            _sideEffect.emit(HealthSideEffect.LaunchPermissions(permissions))
        }
    }

    private var isObservingChanges = false
    private fun observeHealthConnectChanges() {
        if (isObservingChanges) return
        viewModelScope.launch {
            isObservingChanges = true
            val token = healthSessionManager.getChangesToken(setOf(ExerciseSessionRecord::class))
            healthSessionManager.getChanges(token)
                .filterIsInstance<HealthSessionManager.ChangesMessage.ChangeList>()
                .collect {
                    initialLoad() // Reload all data when a change is detected
                }
        }
    }

    private fun readAllDAta() {
        viewModelScope.launch {
            healthSessionManager.logAllHealthData()
        }
    }

    private suspend fun readSessionInputs(): List<ExerciseSessionRecord> {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()
        return healthSessionManager.readExerciseSessions(startOfDay.toInstant(), now)
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
