package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import com.ylabz.basepro.core.model.bike.SuspensionState

// 1. Define the status types (The "What")
enum class BatteryZone {
    UNKNOWN, GOOD, WARNING, CRITICAL
}

data class GlassUiState(
    // --- RAW DATA (Source of Truth) ---
    // We store real numbers here, not Strings.
    val currentGear: Int = 1,
    val suspension: SuspensionState = SuspensionState.OPEN,
    val isBikeConnected: Boolean = false,

    val rawSpeed: Double = 0.0,        // Changed from String to Double
    val rawBattery: Int? = null,       // Int? handles nulls automatically
    val rawMotorPower: Double? = null, // Nullable for "--" logic
    val rawHeartRate: Int? = null,
    val rawHeading: Float = 0f,

    // Keep these as strings if they are pre-formatted durations/distances,
    // or convert them to raw types if you want the same control.
    val tripDistance: String = "0.0",
    val calories: String = "0",
    val rideDuration: String = "00:00",
    val averageSpeed: String = "0.0",
    val currentScreen: ScreenState = ScreenState.HOME
) {
    // =================================================================
    // COMPUTED PROPERTIES (The Logic is Here!)
    // =================================================================

    val formattedSpeed: String
        get() = if (isBikeConnected) String.format("%.1f", rawSpeed) else "--"

    // 2. The Logic: State decides the "Zone"
    val batteryZone: BatteryZone
        get() = when {
            !isBikeConnected || rawBattery == null -> BatteryZone.UNKNOWN
            rawBattery > 50 -> BatteryZone.GOOD
            rawBattery > 20 -> BatteryZone.WARNING
            else -> BatteryZone.CRITICAL
        }

    val formattedBattery: String
        get() = if (isBikeConnected) {
            rawBattery?.let { "$it%" } ?: "--%"
        } else {
            "--%"
        }

    val formattedPower: String
        get() = if (isBikeConnected) {
            // Show power only if > 0
            rawMotorPower?.takeIf { it > 0 }?.let { "${it.toInt()} W" } ?: "--"
        } else {
            "--"
        }

    val formattedHeartRate: String
        get() = if (isBikeConnected) {
            rawHeartRate?.takeIf { it > 0 }?.toString() ?: "--"
        } else {
            "--"
        }

    val formattedHeading: String
        get() {
            if (!isBikeConnected) return "---"
            val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
            val index = ((rawHeading + 22.5) / 45.0).toInt() and 7
            return "${rawHeading.toInt()}° ${directions[index]}"
        }
}

