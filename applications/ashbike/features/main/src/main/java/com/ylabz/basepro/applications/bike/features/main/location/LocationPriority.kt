package com.ylabz.basepro.applications.bike.features.main.location

enum class LocationPriority {
    /**
     * For UI components like a speedometer that need live speed when visible,
     * but not necessarily for persistent background tracking.
     * Suggests balanced power accuracy.
     */
    PASSIVE_UI,

    /**
     * For active ride tracking, typically by a foreground service,
     * requiring higher accuracy and frequency.
     * Suggests high accuracy.
     */
    ACTIVE_TRACKING
}
