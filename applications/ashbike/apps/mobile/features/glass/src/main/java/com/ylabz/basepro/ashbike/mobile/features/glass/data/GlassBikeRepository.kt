package com.ylabz.basepro.ashbike.mobile.features.glass.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

// A Singleton object that lives as long as the app is running
@Singleton // 1. Tells Hilt to share ONE instance across Phone and Glass
class GlassBikeRepository @Inject constructor() { // 2. Tells Hilt how to create it

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

    // 1. New State for Suspension
    private val _suspensionState = MutableStateFlow(SuspensionState.OPEN)
    val suspensionState = _suspensionState.asStateFlow()

    // 2. Logic to cycle modes: Open -> Trail -> Lock -> Open
    fun toggleSuspension() {
        _suspensionState.update { current ->
            when (current) {
                SuspensionState.OPEN -> SuspensionState.TRAIL
                SuspensionState.TRAIL -> SuspensionState.LOCK
                SuspensionState.LOCK -> SuspensionState.OPEN
            }
        }
    }
}