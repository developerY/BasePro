package com.ylabz.basepro.feature.heatlh.ui


import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.service.HealthSessionManager
import com.ylabz.basepro.core.model.health.SleepSessionData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val healthSessionManager: HealthSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HealthUIState>(HealthUIState.Loading)
    val uiState: StateFlow<HealthUIState> = _uiState

    init {
        checkPermissionsAndLoadData()
    }

    fun onEvent(event: HealthEvent) {
        when (event) {
            HealthEvent.RequestPermissions -> requestPermissions()
            HealthEvent.LoadHealthData -> loadHealthData()
            HealthEvent.Retry -> checkPermissionsAndLoadData()
        }
    }

    private fun checkPermissionsAndLoadData() {
        viewModelScope.launch {
            if (healthSessionManager.isAvailable.value != HealthConnectClient.SDK_AVAILABLE) {
                _uiState.value = HealthUIState.Error("Health Connect is not available.")
                return@launch
            }

            val requiredPermissions = healthSessionManager.getRequiredPermissions()
            val hasPermissions = healthSessionManager.hasAllPermissions(requiredPermissions)
            if (hasPermissions) {
                loadHealthData()
            } else {
                _uiState.value = HealthUIState.PermissionsRequired("Health permissions are required.")
            }
        }
    }

    private fun requestPermissions() {
        _uiState.value = HealthUIState.PermissionsRequired("Requesting permissions...")
    }

    private fun loadHealthData() {
        viewModelScope.launch {
            _uiState.value = HealthUIState.Loading
            try {
                val healthData = healthSessionManager.getHealthData()
                val sleepData :  List<SleepSessionData>  = healthSessionManager.readSleepSessions()
                _uiState.value = HealthUIState.Success(sleepData)
            } catch (e: Exception) {
                _uiState.value = HealthUIState.Error("Failed to load health data.")
            }
        }
    }
}
