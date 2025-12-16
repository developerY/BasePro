package com.ylabz.basepro.ashbike.mobile.features.glass.ui

sealed interface GlassUiEvent {
    // Gear Controls
    data object GearUp : GlassUiEvent
    data object GearDown : GlassUiEvent

    // Navigation / App Controls
    data object CloseApp : GlassUiEvent

    // If you add the "Select from List" feature back via ViewModel:
    data class SelectGear(val gear: Int) : GlassUiEvent

    data object ToggleSuspension : GlassUiEvent // <--- NEW
}