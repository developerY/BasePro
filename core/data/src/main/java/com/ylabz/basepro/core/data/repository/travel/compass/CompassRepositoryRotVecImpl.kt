package com.ylabz.basepro.core.data.repository.travel.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CompassRepositoryAccMagImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CompassRepository {

    private val sensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override val headingFlow: Flow<Float> = callbackFlow {
        // Arrays to store accelerometer & magnetometer readings
        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var hasGravity = false
        var hasGeomagnetic = false

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        // Clone to avoid concurrency issues
                        gravity[0] = event.values[0]
                        gravity[1] = event.values[1]
                        gravity[2] = event.values[2]
                        hasGravity = true
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        geomagnetic[0] = event.values[0]
                        geomagnetic[1] = event.values[1]
                        geomagnetic[2] = event.values[2]
                        hasGeomagnetic = true
                    }
                }

                // If we have both readings, compute orientation
                if (hasGravity && hasGeomagnetic) {
                    val rMatrix = FloatArray(9)
                    val iMatrix = FloatArray(9)
                    val success = SensorManager.getRotationMatrix(
                        rMatrix, iMatrix, gravity, geomagnetic
                    )
                    if (success) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(rMatrix, orientation)

                        // orientation[0] = azimuth in radians
                        // Convert to degrees & normalize [0..360)
                        val azimuthRad = orientation[0]
                        val azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                        val heading = (azimuthDeg + 360f) % 360f

                        trySend(heading)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not used, but required by interface
            }
        }

        // Register listeners for both sensors
        // Use a suitable sensor delay (e.g., SENSOR_DELAY_GAME or SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(
            sensorListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
        sensorManager.registerListener(
            sensorListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_GAME
        )

        // Cleanup when the flow collector stops
        awaitClose {
            sensorManager.unregisterListener(sensorListener)
        }
    }
}
