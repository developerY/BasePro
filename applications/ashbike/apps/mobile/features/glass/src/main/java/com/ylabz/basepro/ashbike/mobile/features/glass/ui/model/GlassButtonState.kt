package com.ylabz.basepro.ashbike.mobile.features.glass.ui.model

// Define the 3 states clearly
enum class GlassButtonState {
    NO_GLASSES,      // State 1: Hardware missing
    READY_TO_START,  // State 2: Hardware good, Software stopped
    PROJECTING       // State 3: Hardware good, Software running
}