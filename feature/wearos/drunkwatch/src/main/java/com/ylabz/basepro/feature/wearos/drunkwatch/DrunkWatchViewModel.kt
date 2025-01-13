package com.ylabz.basepro.feature.wearos.drunkwatch

import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectFeatures
import androidx.health.connect.client.feature.ExperimentalFeatureAvailabilityApi
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DrunkWatchViewModel @Inject constructor(
    val healthSessionManager: HealthSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<DrunkWatchUiState>(DrunkWatchUiState.Uninitialized)
    var uiState = _uiState.asStateFlow()

    init {
        initialLoad()
    }

    fun onEvent(event: DrukWatchEvent) {
        when (event) {
            is DrukWatchEvent.LoadDrukWatchData -> loadHealthData()
            is DrukWatchEvent.DeleteAll -> delData()
            is DrukWatchEvent.Insert -> insertExerciseSession()
        }
    }

    private fun loadHealthData() {
        // TODOs("Not yet implemented")
    }

    private fun insertExerciseSession() {
        viewModelScope.launch {
        }
    }


    private fun delData() {
        viewModelScope.launch {
        }
    }


    fun initialLoad() {
        Log.d("HealthViewModel", "initialLoad() called") // Debug statement
        viewModelScope.launch {
            Log.d("HealthViewModel", "viewModelScope.launch called") // Debug statement
            try {
            } catch (e: Exception) {
                Log.e("HealthViewModel", "Exception in initialLoad: ${e.message}", e)
            }
        }
    }


}
