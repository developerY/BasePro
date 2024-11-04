package com.ylabz.basepro.feature.maps.ui

sealed interface MapUIState {
    object Loading : MapUIState
    data class Success(val directions: String) : MapUIState
    data class PartialSuccess(val message: String) : MapUIState // Directions failed, but map loads
    data class Error(val message: String) : MapUIState // Full failure
}
