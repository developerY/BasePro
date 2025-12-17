package com.ylabz.basepro.ashbike.mobile.features.glass.ui

sealed interface GlassUiEvent {
    // Business Logic (Handled by ViewModel)
    data object GearUp : GlassUiEvent
    data object GearDown : GlassUiEvent
    data object ToggleSuspension : GlassUiEvent
    data class SelectGear(val gear: Int) : GlassUiEvent

    // Navigation / System Actions (Intercepted by GlassApp)
    data object OpenGearList : GlassUiEvent
    data object CloseApp : GlassUiEvent
}