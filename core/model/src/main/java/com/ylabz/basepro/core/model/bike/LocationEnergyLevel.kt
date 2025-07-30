package com.ylabz.basepro.core.model.bike

enum class LocationEnergyLevel(
    val displayName: String, // For UI if needed, or just for clarity
    // Intervals for when a formal ride is active
    val activeRideIntervalMillis: Long,
    val activeRideMinUpdateIntervalMillis: Long,
    // Intervals for when no formal ride is active (continuous/passive)
    val passiveTrackingIntervalMillis: Long,
    val passiveTrackingMinUpdateIntervalMillis: Long
) {
    POWER_SAVER(
        displayName = "Power Saver",
        activeRideIntervalMillis = 8000L, // e.g., 8 seconds
        activeRideMinUpdateIntervalMillis = 4000L,
        passiveTrackingIntervalMillis = 30000L, // e.g., 30 seconds
        passiveTrackingMinUpdateIntervalMillis = 15000L
    ),
    BALANCED( // This will be our default and match current hardcoded values
        displayName = "Balanced",
        activeRideIntervalMillis = 2000L, // 2 seconds
        activeRideMinUpdateIntervalMillis = 1000L,
        passiveTrackingIntervalMillis = 5000L, // 5 seconds
        passiveTrackingMinUpdateIntervalMillis = 2500L
    ),
    HIGH_ACCURACY(
        displayName = "High Accuracy",
        activeRideIntervalMillis = 1000L, // 1 second
        activeRideMinUpdateIntervalMillis = 500L, // 0.5 seconds
        passiveTrackingIntervalMillis = 2000L, // 2 seconds for passive to also be high
        passiveTrackingMinUpdateIntervalMillis = 1000L
    );

    companion object {
        // Helper to get enum from stored ordinal, defaulting to BALANCED
        fun fromOrdinal(ordinal: Int): LocationEnergyLevel {
            return entries.getOrElse(ordinal) { BALANCED }
        }
    }
}
