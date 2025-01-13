package com.ylabz.basepro.feature.wearos.sleepwatch

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
class SleepWatchViewModel @Inject constructor(
    val healthSessionManager: HealthSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SleepWatchUiState>(SleepWatchUiState.Uninitialized)
    var uiState = _uiState.asStateFlow()

    init {
        initialLoad()
    }

    fun onEvent(event: SleepWatchEvent) {
        when (event) {
            is SleepWatchEvent.LoadSleepWatchData -> loadHealthData()
            is SleepWatchEvent.DeleteAll -> delData()
            is SleepWatchEvent.Insert -> insertExerciseSession()
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

    private fun loadHealthData() {
        _uiState.value = SleepWatchUiState.Loading
        viewModelScope.launch {
            try {
                //val weightInputs = readWeightInputs()
                // You can include more data reading functions here
                _uiState.value = SleepWatchUiState.Success(readSessionInputs())
            } catch (e: Exception) {
                _uiState.value = SleepWatchUiState.Error("Failed to load health data: ${e.message}")
            }
        }
    }

    private suspend fun readSessionInputs(): List<ExerciseSessionRecord> {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()
        val endofWeek = startOfDay.toInstant().plus(7, ChronoUnit.DAYS)
        val sessionInputs = healthSessionManager.readExerciseSessions(startOfDay.toInstant(), now)
        print("weightInputs: $sessionInputs")
        Log.d("TAG","${healthSessionManager.readWeightInputs(startOfDay.toInstant(), now)}")
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


    fun insertExerciseSession() {
        viewModelScope.launch {

        }
    }

    fun deleteStoredExerciseSession(uid: String) {
        viewModelScope.launch {

        }
    }
}
