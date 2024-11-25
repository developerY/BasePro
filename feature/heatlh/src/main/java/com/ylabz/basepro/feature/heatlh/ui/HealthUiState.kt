package com.ylabz.basepro.feature.heatlh.ui

import androidx.health.connect.client.records.WeightRecord
import java.util.UUID

sealed class HealthUiState {
    object Uninitialized : HealthUiState()
    object Loading : HealthUiState()
    object Done : HealthUiState()
    data class PermissionsRequired(val message: String) : HealthUiState()
    data class Success(val healthData: List<WeightRecord>) : HealthUiState()
    sealed class Error : HealthUiState() {
        data class Message(val message: String) : Error()
        data class Exception(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : Error()
    }

    // data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : HealthUiState()
    // A random UUID is used in each Error object to allow errors to be uniquely identified,
    // and recomposition won't result in multiple snackbars.
    // data class Error(val message: String) : HealthUiState()
    // data class Error(val message: String) : HealthUiState()
    // data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : HealthUiState()
    // object Done : HealthUiState()


}
