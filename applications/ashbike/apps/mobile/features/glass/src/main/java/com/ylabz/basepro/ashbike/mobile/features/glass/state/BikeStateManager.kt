package com.ylabz.basepro.ashbike.mobile.features.glass.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// A Singleton object that lives as long as the app is running
object BikeStateManager {

    // 1. New State: Is the Glass UI currently running?
    private val _isGlassActive = MutableStateFlow(false)
    val isGlassActive: StateFlow<Boolean> = _isGlassActive.asStateFlow()

    fun setGlassActive(isActive: Boolean) {
        _isGlassActive.value = isActive
    }
    private val _currentGear = MutableStateFlow(1) // Default Gear 1
    val currentGear: StateFlow<Int> = _currentGear.asStateFlow()

    fun gearUp() {
        _currentGear.update { current ->
            if (current < 12) current + 1 else current
        }
    }

    fun gearDown() {
        _currentGear.update { current ->
            if (current > 1) current - 1 else current
        }
    }
}