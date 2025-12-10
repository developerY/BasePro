package com.ylabz.basepro.ashbike.wear.presentation

import kotlin.jvm.java
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.ylabz.basepro.ashbike.wear.service.ExerciseService
import com.ylabz.basepro.ashbike.wear.service.ExerciseMetrics

@Composable
fun WearBikeScreen() {
    val context = LocalContext.current
    var service by remember { mutableStateOf<ExerciseService?>(null) }
    val metrics by service?.exerciseMetrics?.collectAsState(initial = ExerciseMetrics())
        ?: remember { mutableStateOf(ExerciseMetrics()) }

    // Bind to Service
    DisposableEffect(context) {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, binder: IBinder) {
                service = (binder as ExerciseService.LocalBinder).getService()
            }
            override fun onServiceDisconnected(arg0: ComponentName) {
                service = null
            }
        }
        val intent = Intent(context, ExerciseService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        onDispose { context.unbindService(connection) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("AshBike Live", style = MaterialTheme.typography.title3)

        // Data Stats
        Text("HR: ${metrics.heartRate.toInt()} bpm")
        Text("Speed: ${String.format("%.1f", metrics.speed * 3.6)} km/h") // m/s to km/h
        Text("Dist: ${String.format("%.2f", metrics.distance / 1000)} km")

        // Controls
        Button(
            onClick = { service?.startExercise() },
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text("Start Ride")
        }
        Button(
            onClick = { service?.stopExercise() },
            colors = ButtonDefaults.secondaryButtonColors()
        ) {
            Text("Stop")
        }
    }
}