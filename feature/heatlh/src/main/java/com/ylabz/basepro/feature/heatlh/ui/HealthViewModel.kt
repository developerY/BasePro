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

// Add this sealed interface inside or outside the HealthViewModel class
sealed interface HealthSideEffect {
    data class LaunchPermissions(val permissions: Set<String>) : HealthSideEffect
}

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
    // inside HealthViewModel
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


    val permissionsLauncher = healthSessionManager.requestPermissionsActivityContract()


    private fun checkHealthConnectAvailability() {
        val availability = healthSessionManager.availability.value
        if (availability != HealthConnectClient.SDK_AVAILABLE) {
            _uiState.value = HealthUiState.Error("Health Connect is not available.")
        }
    }

    // 1. ADD a SharedFlow for one-time side effects
    private val _sideEffect = MutableSharedFlow<HealthSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    // 2. REMOVE initialLoad() and observeHealthConnectChanges() from the init block
    init {
        // Only check for availability and then let the UI trigger the permission check/load.
        checkHealthConnectAvailability()
        // By removing initialLoad(), the ViewModel will wait for the UI to tell it what to do.
        // observeHealthConnectChanges() // <--- THIS IS THE PROBLEM
    }

    fun onEvent(event: HealthEvent) {
        when (event) {
            is HealthEvent.LoadHealthData -> loadHealthData()
            is HealthEvent.DeleteAll -> delData()
            is HealthEvent.Retry -> checkPermissionsAndLoadData()
            is HealthEvent.TestInsert -> {
                insertTestExerciseSession()
            }
            is HealthEvent.Insert -> insertExerciseSession(event)

            is HealthEvent.RequestPermissions -> checkPermissionsAndLoadData()

            is HealthEvent.ReadAll ->  readAllDAta()
        }
    }


    // 3. CREATE a new function to handle the permission request event
    private fun requestPermissions() {
        viewModelScope.launch {
            val hasPerms = healthSessionManager.hasAllPermissions(permissions)
            if (!hasPerms) {
                // If we don't have permissions, emit the side effect to launch the dialog
                _sideEffect.emit(HealthSideEffect.LaunchPermissions(permissions))
            } else {
                // If we already have permissions, just refresh the data
                loadHealthData()
            }
        }
    }

    private fun observeHealthConnectChanges() = viewModelScope.launch {
        // Get a token for all the types you care about
        val token = healthSessionManager.getChangesToken(
            setOf(ExerciseSessionRecord::class)
        )

        // Subscribe to change messages
        healthSessionManager.getChanges(token)
            .filterIsInstance<HealthSessionManager.ChangesMessage.ChangeList>()   // only when there really *are* changes
            .collect { changeList ->
                // If *any* of those changes are for sessions, re-load
                if (changeList.changes.any { it == ExerciseSessionRecord::class }) {
                    loadHealthData()
                }
            }
    }


    // The insert function remains a great way to handle the sync icon click
    private fun insertExerciseSession(event: HealthEvent.Insert) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                // Log it
                // 1️⃣ Insert the pre-built Record list
                // healthSessionManager.insertRecords(event.records)
                val res = healthSessionManager.insertRecords(event.records)
                Log.d("DebugSync", "  <- insertRecords response IDs=${res.recordIdsList}")
                // then reload
                // 2️⃣ Refresh so UI shows the new data

                /* Now read back the sessions you just wrote
                val sessions = healthSessionManager.readExerciseSessions(Instant.EPOCH, Instant.now())
                sessions
                    .filter { it.metadata.id in res.recordIdsList }
                    .forEach {
                        Log.d("HealthViewModel", "SessionMetadata post-insert: id=${it.metadata.id}, origin=${it.metadata.dataOrigin}")
                    }*/

                loadHealthData()
            }
        }
    }

    private fun readAllDAta() {
        viewModelScope.launch {
            healthSessionManager.logAllHealthData()
        }
    }

    // Keep track of whether you're already observing changes
    private var isObservingChanges = false

    private fun checkPermissionsAndLoadData() {
        viewModelScope.launch {
            if (healthSessionManager.availability.value != HealthConnectClient.SDK_AVAILABLE) {
                _uiState.value = HealthUiState.Error("Health Connect is not available.")
                return@launch
            }

            val permissionsGranted = healthSessionManager.hasAllPermissions(permissions)
            if (permissionsGranted) {
                loadHealthData()
                // *** ADD THIS: Only start observing after permissions are confirmed ***
                if (!isObservingChanges) {
                    observeHealthConnectChanges()
                    isObservingChanges = true
                }
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
        viewModelScope.launch {
            try {
                tryWithPermissionsCheck {
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
            val sessions = readSessionInputs()
            try {
                _uiState.value = HealthUiState.Success(readSessionInputs())
            } catch (e: Exception) {
                _uiState.value = HealthUiState.Error("Failed to load health data: ${e.message}")
            }
        }
    }

    // TODO : make this for all time
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

    // This function will now be wrapped in a permission check that can trigger the launch
    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        val hasPermissions = healthSessionManager.hasAllPermissions(permissions)
        if (hasPermissions) {
            try {
                block()
            } catch (e: Exception) {
                _uiState.value = HealthUiState.Error("Action failed: ${e.message}")
            }
        } else {
            // When trying to sync without permissions, trigger the launch effect
            _sideEffect.emit(HealthSideEffect.LaunchPermissions(permissions))
        }
    }



    fun updatePermissionsState(grantedPermissions: Set<String>) {
        permissionsGranted.value = permissions.all { it in grantedPermissions }
        backgroundReadGranted.value = backgroundReadPermissions.all { it in grantedPermissions }
    }

    fun insertTestExerciseSession() {
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

                healthSessionManager.writeExerciseSessionTest() //startOfSession, endOfSession)
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
