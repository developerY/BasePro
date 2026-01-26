package com.ylabz.basepro.core.data.repository.bike

import com.ylabz.basepro.core.model.bike.BikeRideInfo
import kotlin.random.Random

/**
 * ISOLATED DEMO LOGIC
 * Keeps the main ViewModel clean. Delete this class (or stop calling it) after the video.
 */
class DemoModeSimulator {

    private var demoToggle = false
    private var demoBattery = 105

    /**
     * Takes real GPS data and overlays fake Bike connection data.
     */
    fun process(originalInfo: BikeRideInfo): BikeRideInfo {
        // 1. Flip Connection State
        demoToggle = !demoToggle

        // 2. Logic when connected (Drain Battery)
        if (demoToggle) {
            demoBattery -= 5
            if (demoBattery < 0) demoBattery = 100
        }

        // 3. Random Motor Power (210W - 290W)
        val randomMotor = if (demoToggle) {
            Random.nextInt(210, 290).toFloat()
        } else {
            0f
        }

        // 4. Return the merged data
        return originalInfo.copy(
            isBikeConnected = demoToggle,
            batteryLevel = if (demoToggle) demoBattery else null,
            motorPower = randomMotor,
            // IMPORTANT: We preserve the real GPS speed/distance
            currentSpeed = originalInfo.currentSpeed
        )
    }
}