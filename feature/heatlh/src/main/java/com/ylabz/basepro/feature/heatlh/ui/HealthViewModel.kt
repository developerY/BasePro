package com.ylabz.basepro.feature.heatlh.ui

import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectFeatures
import androidx.health.connect.client.feature.ExperimentalFeatureAvailabilityApi
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.permission.HealthPermission.Companion.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.service.health.HealthSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val healthSessionManager: HealthSessionManager
) : ViewModel() {

    private fun checkHealthConnectAvailability() {
        val availability = healthSessionManager.availability.value
        if (availability != HealthConnectClient.SDK_AVAILABLE) {
            //healthUiState = HealthUiState.Error("Health Connect is not available.")
        }
    }

    val permissions = setOf(
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
    )

    val backgroundReadPermissions = setOf(PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND)

    var permissionsGranted = mutableStateOf(false)
        private set

    var backgroundReadAvailable = mutableStateOf(false)
        private set

    var backgroundReadGranted = mutableStateOf(false)
        private set

    var sessionsList: MutableState<List<ExerciseSessionRecord>> = mutableStateOf(listOf())
        private set

    var healthUiState: HealthUiState by mutableStateOf(HealthUiState.Uninitialized)
        private set

    val permissionsLauncher = healthSessionManager.requestPermissionsActivityContract()


    fun initialLoad() {
        Log.d("HealthViewModel", "initialLoad() called") // Debug statement
        viewModelScope.launch {
            Log.d("HealthViewModel", "viewModelScope.launch called") // Debug statement
            try {
                tryWithPermissionsCheck {
                    Log.d("HealthViewModel", "Permissions check passed, loading data") // Debug
                    readWeightInputs()
                }
            } catch (e: Exception) {
                Log.e("HealthViewModel", "Exception in initialLoad: ${e.message}", e)
            }
        }
    }


    private suspend fun readWeightInputs() {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()
        val endofWeek = startOfDay.toInstant().plus(7, ChronoUnit.DAYS)
        val weightInputs = healthSessionManager.readWeightInputs(startOfDay.toInstant(), now)
        print("weightInputs: $weightInputs")
         Log.d("TAG","${healthSessionManager.readWeightInputs(startOfDay.toInstant(), now)}")
    }

    @OptIn(ExperimentalFeatureAvailabilityApi::class)
    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        Log.d("HealthViewModel", "tryWithPermissionsCheck called")

        // Logging permission checks
        permissionsGranted.value = healthSessionManager.hasAllPermissions(permissions)
        Log.d("HealthViewModel", "Permissions granted: ${permissionsGranted.value}")

        backgroundReadAvailable.value = healthSessionManager.isFeatureAvailable(
            HealthConnectFeatures.FEATURE_READ_HEALTH_DATA_IN_BACKGROUND
        )
        Log.d("HealthViewModel", "Background read available: ${backgroundReadAvailable.value}")

        backgroundReadGranted.value = healthSessionManager.hasAllPermissions(backgroundReadPermissions)
        Log.d("HealthViewModel", "Background read permissions granted: ${backgroundReadGranted.value}")

        healthUiState = try {
            if (permissionsGranted.value) {
                Log.d("HealthViewModel", "Permissions granted, executing block")
                block() // Execute the provided block if permissions are granted
            } else {
                Log.d("HealthViewModel", "Permissions not granted, updating UI state to request permissions")
                HealthUiState.PermissionsRequired("Permissions are required.")
            }
            HealthUiState.Done
        } catch (remoteException: RemoteException) {
            Log.e("HealthViewModel", "RemoteException caught: ${remoteException.message}", remoteException)
            HealthUiState.Error(remoteException)
        } catch (securityException: SecurityException) {
            Log.e("HealthViewModel", "SecurityException caught: ${securityException.message}", securityException)
            HealthUiState.Error(securityException)
        } catch (ioException: IOException) {
            Log.e("HealthViewModel", "IOException caught: ${ioException.message}", ioException)
            HealthUiState.Error(ioException)
        } catch (illegalStateException: IllegalStateException) {
            Log.e("HealthViewModel", "IllegalStateException caught: ${illegalStateException.message}", illegalStateException)
            HealthUiState.Error(illegalStateException)
        }
    }



    /*fun onEvent(event: HealthEvent) {
        when (event) {
            HealthEvent.RequestPermissions -> requestPermissions()
            HealthEvent.LoadHealthData -> loadHealthData()
            HealthEvent.Retry -> checkPermissionsAndLoadData()
        }
    }*/

    private fun checkPermissionsAndLoadData() {
        viewModelScope.launch {
            /*if (healthSessionManager.isAvailable.value != HealthConnectClient.SDK_AVAILABLE) {
                _healthUiState.value = HealthhealthUiState.Error("Health Connect is not available.")
                return@launch
            }

            val requiredPermissions = healthSessionManager.getRequiredPermissions()
            val hasPermissions = healthSessionManager.hasAllPermissions(requiredPermissions)
            if (hasPermissions) {
                loadHealthData()
            } else {
                _healthUiState.value = HealthhealthUiState.PermissionsRequired("Health permissions are required.")
            }*/
        }
    }


}
