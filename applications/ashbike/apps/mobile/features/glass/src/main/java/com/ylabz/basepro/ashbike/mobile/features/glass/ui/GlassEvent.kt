package com.ylabz.basepro.ashbike.mobile.features.glass.ui

sealed interface GlassEvent {
    // Gear Controls
    data object GearUp : GlassEvent
    data object GearDown : GlassEvent

    // Navigation / App Controls
    data object CloseApp : GlassEvent

    // If you add the "Select from List" feature back via ViewModel:
    data class SelectGear(val gear: Int) : GlassEvent
}