package com.ylabz.basepro.maps.ui

sealed interface MapEvent {
    object LoadData : MapEvent
    object OnRetry : MapEvent
    data class UpdateDirections(val org: String, val des: String) : MapEvent
}
