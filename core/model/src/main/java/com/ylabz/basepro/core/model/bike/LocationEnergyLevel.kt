package com.ylabz.basepro.core.model.bike

enum class LocationEnergyLevel(
    val activeRideIntervalMillis: Long,
    val passiveTrackingIntervalMillis: Long,
    val activeRideMinUpdateIntervalMillis: Long,
    val passiveTrackingMinUpdateIntervalMillis: Long,
    val isAutoMode: Boolean = false
) {
    POWER_SAVER(10000L, 600000L, 5000L, 300000L),
    BALANCED(5000L, 60000L, 5000L, 30000L),
    HIGH_ACCURACY(2000L, 2000L, 2000L, 2000L),
    AUTO(5000L, 60000L, 5000L, 30000L, isAutoMode = true)
}
