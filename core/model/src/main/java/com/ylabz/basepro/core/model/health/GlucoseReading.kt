package com.ylabz.basepro.core.model.health

import java.time.Instant

data class GlucoseReading(
    val valueMgDl: Float,      // Glucose in mg/dL
    val timestamp: Instant,
    val source: GlucoseSource,
    val trendArrow: String? = null // e.g., "RISING", "STABLE" (Common in Libre)
)

enum class GlucoseSource {
    BLE_STANDARD,
    LIBRE_NFC,
    LIBRE_BLE,
    SIMULATOR
}