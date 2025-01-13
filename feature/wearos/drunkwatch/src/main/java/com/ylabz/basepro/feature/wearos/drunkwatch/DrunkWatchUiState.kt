package com.ylabz.basepro.feature.wearos.drunkwatch

import androidx.health.connect.client.records.ExerciseSessionRecord
import java.util.UUID

sealed class DrunkWatchUiState {
    object Uninitialized : DrunkWatchUiState()
    object Loading : DrunkWatchUiState()
    data class Success(val healthData: List<ExerciseSessionRecord>) : DrunkWatchUiState()
    data class Error(val message: String, val uuid: UUID = UUID.randomUUID()) : DrunkWatchUiState()
}
