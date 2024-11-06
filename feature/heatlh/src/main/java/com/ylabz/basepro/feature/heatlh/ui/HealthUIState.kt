package com.ylabz.basepro.feature.heatlh.ui

import com.ylabz.basepro.core.model.health.SleepSessionData

sealed interface HealthUIState {
    object Loading : HealthUIState
    data class PermissionsRequired(val message: String) : HealthUIState
    data class Success(val healthData: List<SleepSessionData>) : HealthUIState
    data class Error(val message: String) : HealthUIState
}
