package com.ylabz.basepro.core.data.repository.travel.compass

import java.lang.Math.toDegrees
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompassRepositoryRotVecImpl @Inject constructor(
    private val context: Context
) : CompassRepository {

    override val headingFlow: Flow<Float> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (rotationSensor == null) {
            // If the sensor is not available, close the flow with an exception.
            close(Exception("Rotation vector sensor not available"))
            return@callbackFlow
        }

        val sensorListener = object : SensorEventListener {
            private val rotationMatrix = FloatArray(9)
            private val orientationAngles = FloatArray(3)

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    // The first value is the azimuth (in radians). Convert it to degrees.
                    val azimuthDegrees = toDegrees(orientationAngles[0].toDouble()).toFloat()
                    trySend(azimuthDegrees)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No-op for this implementation.
            }
        }

        sensorManager.registerListener(
            sensorListener,
            rotationSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        // Clean up when the flow collector is cancelled.
        awaitClose {
            sensorManager.unregisterListener(sensorListener)
        }
    }
}
