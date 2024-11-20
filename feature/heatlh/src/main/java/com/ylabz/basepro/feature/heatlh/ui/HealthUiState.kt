package com.ylabz.basepro.feature.heatlh.ui

import com.ylabz.basepro.core.model.health.SleepSessionData
import java.util.UUID

sealed class HealthUiState {
    object Loading : HealthUiState()
    data class PermissionsRequired(val message: String) : HealthUiState()
    data class Success(val healthData: List<SleepSessionData>) : HealthUiState()
    //data class Error(val message: String) : HealthUiState()

    object Uninitialized : HealthUiState()
    object Done : HealthUiState()

    // A random UUID is used in each Error object to allow errors to be uniquely identified,
    // and recomposition won't result in multiple snackbars.
    data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : HealthUiState()
}
