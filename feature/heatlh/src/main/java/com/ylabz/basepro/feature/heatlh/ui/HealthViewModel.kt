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

@HiltViewModel
class HealthViewModel @Inject constructor(
    @ApplicationContext private val appCtx: Context,
    val healthSessionManager: HealthSessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HealthUiState>(HealthUiState.Uninitialized)
    var uiState = _uiState.asStateFlow()

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

    var permissionsGranted = mutableStateOf(false)
        private set

    var backgroundReadAvailable = mutableStateOf(false)
        private set

    var backgroundReadGranted = mutableStateOf(false)

    /** Expose the set of synced session IDs for the UI to consume directly */
    val syncedIds: StateFlow<Set<String>> = uiState
        .map { state ->
            (state as? HealthUiState.Success)
                ?.healthData
                ?.map { it.metadata.id }
                ?.toSet()
                .orEmpty()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptySet()
        )


    val permissionsLauncher = healthSessionManager.requestPermissionsActivityContract()


    private fun checkHealthConnectAvailability() {
        val availability = healthSessionManager.availability.value
        if (availability != HealthConnectClient.SDK_AVAILABLE) {
            _uiState.value = HealthUiState.Error("Health Connect is not available.")
        }
    }

    init {
        checkHealthConnectAvailability()
        initialLoad()
    }

    fun onEvent(event: HealthEvent) {
        Log.d("HealthViewModel", "onEvent() called with event: $event") // Debug statement
        when (event) {
            is HealthEvent.LoadHealthData -> loadHealthData()
            is HealthEvent.DeleteAll -> delData()
            is HealthEvent.Retry -> checkPermissionsAndLoadData()
            is HealthEvent.TestInsert -> {
                Log.d("HealthViewModel", "insertExerciseSession() called") // Debug statement
                insertExerciseSession()
            }
            is HealthEvent.Insert -> viewModelScope.launch {
                tryWithPermissionsCheck {
                    // 1️⃣ Insert the pre-built Record list
                    healthSessionManager.insertRecords(event.records)
                    // 2️⃣ Refresh so UI shows the new data
                    loadHealthData()
                }
            }

            is HealthEvent.RequestPermissions -> checkPermissionsAndLoadData()

            is HealthEvent.ReadAll ->  readAllDAta()
        }
    }


    private fun readAllDAta() {
        viewModelScope.launch {
            healthSessionManager.logAllHealthData()
        }
    }

    private fun checkPermissionsAndLoadData() {
        viewModelScope.launch {
            if (healthSessionManager.availability.value != HealthConnectClient.SDK_AVAILABLE) {
                _uiState.value = HealthUiState.Error("Health Connect is not available.")
                return@launch
            }

            val permissionsGranted = healthSessionManager.hasAllPermissions(permissions)
            if (permissionsGranted) {
                loadHealthData()
            } else {
                _uiState.value = HealthUiState.PermissionsRequired("Health permissions are required.")
            }
        }
    }


    private fun delData() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthSessionManager.deleteAllSessionData()
            }
        }
    }


    fun initialLoad() {
        Log.d("HealthViewModel", "initialLoad() called") // Debug statement
        Log.d("HC onEvent", "SDK status = ${HealthConnectClient.getSdkStatus(appCtx)}")

        viewModelScope.launch {
            Log.d("HealthViewModel", "viewModelScope.launch called") // Debug statement
            try {
                tryWithPermissionsCheck {
                    Log.d("HealthViewModel", "Permissions check passed, loading data") // Debug
                    readSessionInputs()
                }
            } catch (e: Exception) {
                Log.e("HealthViewModel", "Exception in initialLoad: ${e.message}", e)
            }
        }
    }

    private fun loadHealthData() {
        _uiState.value = HealthUiState.Loading
        viewModelScope.launch {
            try {
                //val weightInputs = readWeightInputs()
                // You can include more data reading functions here
                _uiState.value = HealthUiState.Success(readSessionInputs())
            } catch (e: Exception) {
                _uiState.value = HealthUiState.Error("Failed to load health data: ${e.message}")
            }
        }
    }

    private suspend fun readSessionInputs(): List<ExerciseSessionRecord> {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()
        val endofWeek = startOfDay.toInstant().plus(7, ChronoUnit.DAYS)
        val lastWeek = startOfDay.toInstant().minus(7, ChronoUnit.DAYS)
        val sessionInputs = healthSessionManager.readExerciseSessions(startOfDay.toInstant(), now)
        Log.d("TAG","Did we get anything")
        Log.d("TAG","${healthSessionManager.readExerciseSessions(lastWeek, now).size}")
        Log.d("TAG","${healthSessionManager.readExerciseSessions(startOfDay.toInstant(), now).size}")
        return sessionInputs
    }


    private suspend fun readWeightInputs(): List<WeightRecord> {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()
        val endofWeek = startOfDay.toInstant().plus(7, ChronoUnit.DAYS)
        val weightInputs = healthSessionManager.readWeightInputs(startOfDay.toInstant(), now)
        print("weightInputs: $weightInputs")
         Log.d("TAG","${healthSessionManager.readWeightInputs(startOfDay.toInstant(), now)}")
        return weightInputs
    }

    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        permissionsGranted.value = healthSessionManager.hasAllPermissions(permissions)
        backgroundReadAvailable.value = healthSessionManager.isFeatureAvailable(
            HealthConnectFeatures.FEATURE_READ_HEALTH_DATA_IN_BACKGROUND
        )
        backgroundReadGranted.value = healthSessionManager.hasAllPermissions(backgroundReadPermissions)

        _uiState.value = try {
            if (permissionsGranted.value) {
                block()
                HealthUiState.Success(readSessionInputs())
            } else {
                HealthUiState.PermissionsRequired("permissions")
            }
        } catch (remoteException: RemoteException) {
            HealthUiState.Error(Exception(remoteException).message.toString())
        } catch (securityException: SecurityException) {
            HealthUiState.Error(Exception(securityException).message.toString())
        } catch (ioException: IOException) {
            HealthUiState.Error(Exception(ioException).message.toString())
        } catch (illegalStateException: IllegalStateException) {
            HealthUiState.Error(Exception(illegalStateException).message.toString())
        }
    }



    fun updatePermissionsState(grantedPermissions: Set<String>) {
        permissionsGranted.value = permissions.all { it in grantedPermissions }
        backgroundReadGranted.value = backgroundReadPermissions.all { it in grantedPermissions }
    }

    fun insertExerciseSession() {
        Log.d("HealthViewModel", "insertExerciseSession() called") // Debug statement
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                val latestStartOfSession = ZonedDateTime.now().minusMinutes(30)
                val offset = Random.nextDouble()

                // Generate random start time between the start of the day and (now - 30mins).
                val startOfSession = startOfDay.plusSeconds(
                    (Duration.between(startOfDay, latestStartOfSession).seconds * offset).toLong()
                )
                val endOfSession = startOfSession.plusMinutes(30)

                healthSessionManager.writeExerciseSessionMark() //startOfSession, endOfSession)
            }
        }
    }

    fun deleteStoredExerciseSession(uid: String) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthSessionManager.deleteExerciseSession(uid)
            }
        }
    }
}
