package com.ylabz.basepro.core.model.bike

// You can set all GPS tracking intervals by modifying this single constant:
// THE SINGLE NUMBER: You can set all GPS tracking intervals by modifying this single constant:
// private const val GPS_BASE_UNIT_MS = 250L
// or hit twice as much
private const val GPS_BASE_UNIT_MS = 250L
private const val DEV_PASSIVE_UNIT_MULTIPLIER = 2 // set by eye


// ALIASES: Defined as top-level constants so they are available immediately.
private const val ACTIVE_UNIT_MULTIPLIER = GPS_BASE_UNIT_MS
private const val PASSIVE_UNIT_MULTIPLIER = GPS_BASE_UNIT_MS
enum class LocationEnergyLevel(
    val activeRideIntervalMillis: Long,
    val passiveTrackingIntervalMillis: Long,
    val activeRideMinUpdateIntervalMillis: Long,
    val passiveTrackingMinUpdateIntervalMillis: Long,
    val isAutoMode: Boolean = false
) {
    // 5000ms, 600000ms, 5000ms, 600000ms
    POWER_SAVER(
        ACTIVE_UNIT_MULTIPLIER * 20,
        PASSIVE_UNIT_MULTIPLIER * 40,
        ACTIVE_UNIT_MULTIPLIER * 20,
        PASSIVE_UNIT_MULTIPLIER * 40
    ),

    // 3000ms, 120000ms, 3000ms, 120000ms
    BALANCED(
        ACTIVE_UNIT_MULTIPLIER * 12,
        PASSIVE_UNIT_MULTIPLIER * 20,
        ACTIVE_UNIT_MULTIPLIER * 12,
        PASSIVE_UNIT_MULTIPLIER * 20
    ),

    // 750ms, 30000ms, 750ms, 30000ms (Your new doubled frequency value for high accuracy)
    HIGH_ACCURACY(
        ACTIVE_UNIT_MULTIPLIER * 3,
        PASSIVE_UNIT_MULTIPLIER * 10,
        ACTIVE_UNIT_MULTIPLIER * 3,
        PASSIVE_UNIT_MULTIPLIER * 10
    ),

    // 3000ms, 120000ms, 3000ms, 120000ms
    AUTO(
        ACTIVE_UNIT_MULTIPLIER * 12,
        PASSIVE_UNIT_MULTIPLIER * 40,
        ACTIVE_UNIT_MULTIPLIER * 12,
        PASSIVE_UNIT_MULTIPLIER * 40,
        isAutoMode = true
    )
}

enum class LocationEnergyLevelHardCode(
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