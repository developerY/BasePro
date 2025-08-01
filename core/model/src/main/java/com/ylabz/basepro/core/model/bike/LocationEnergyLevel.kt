package com.ylabz.basepro.core.model.bike

enum class LocationEnergyLevel(
    val activeRideIntervalMillis: Long,
    val passiveTrackingIntervalMillis: Long,
    val activeRideMinUpdateIntervalMillis: Long,
    val passiveTrackingMinUpdateIntervalMillis: Long,
    val isAutoMode: Boolean = false
) {
    POWER_SAVER(5000L, 600000L, 5000L, 600000L),
    BALANCED(3000L, 120000L, 3000L, 120000L),
    HIGH_ACCURACY(1500L, 30000L, 1500L, 30000L),
    AUTO(3000L, 120000L, 3000L, 120000L, isAutoMode = true)
}
