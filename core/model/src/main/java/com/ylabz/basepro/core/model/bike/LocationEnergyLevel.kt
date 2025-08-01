package com.ylabz.basepro.core.model.bike

enum class LocationEnergyLevel(
    val activeRideIntervalMillis: Long,
    val passiveTrackingIntervalMillis: Long,
    val activeRideMinUpdateIntervalMillis: Long,
    val passiveTrackingMinUpdateIntervalMillis: Long,
    val isAutoMode: Boolean = false // New flag, default to false
) {
    POWER_SAVER(60000L, 180000L, 30000L, 60000L, isAutoMode = false),
    BALANCED(30000L, 60000L, 15000L, 30000L, isAutoMode = false),
    HIGH_ACCURACY(2000L, 30000L, 2000L, 15000L, isAutoMode = false),
    AUTO(0L, 0L, 0L, 0L, isAutoMode = true); // Dummy intervals for AUTO, actual are dynamic
}
