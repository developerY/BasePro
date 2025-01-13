package com.ylabz.basepro.feature.wearos.sleepwatch

import androidx.health.connect.client.records.ExerciseSessionRecord
import java.util.UUID

sealed class SleepWatchUiState {
    object Uninitialized : SleepWatchUiState()
    object Loading : SleepWatchUiState()
    data class Success(val healthData: List<ExerciseSessionRecord>) : SleepWatchUiState()
    //data class Error(val message: String) : HealthUiState()
    //object GetPermissions : HealthUiState()
    data class Error(val message: String, val uuid: UUID = UUID.randomUUID()) : SleepWatchUiState()



    /*sealed class Error : HealthUiState() {
        data class Message(val message: String) : Error()
        data class Exception(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : Error()
    }*/

    // data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : HealthUiState()
    // A random UUID is used in each Error object to allow errors to be uniquely identified,
    // and recomposition won't result in multiple snackbars.
    // data class Error(val message: String) : HealthUiState()
    // data class Error(val message: String) : HealthUiState()
    // data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : HealthUiState()
    // object Done : HealthUiState()


}
