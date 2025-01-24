package com.ylabz.basepro.core.data.repository.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ylabz.basepro.core.model.alarm.ProAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject


class AlarmRepositoryImpl @Inject constructor(
    private val context: Context
) : AlarmRepository {
    private val alarmsStateFlow = MutableStateFlow<List<ProAlarm>>(emptyList())

    val Context.dataStore by preferencesDataStore(name = "alarms_datastore")
    private val alarmsKey = stringPreferencesKey("alarms_key")

    init {
        // Continuously observe the DataStore and update alarmsStateFlow
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.data.map { preferences ->
                val alarmsJson = preferences[alarmsKey] ?: "[]"
                Json.decodeFromString<List<ProAlarm>>(alarmsJson)
            }.collect { alarms ->
                alarmsStateFlow.value = alarms
            }
        }
    }

    override fun getAlarms(): Flow<List<ProAlarm>> = alarmsStateFlow

    // Add a new alarm
    override suspend fun addAlarm(alarm: ProAlarm) {
        val currentAlarms = alarmsStateFlow.value
        saveAlarms(currentAlarms + alarm)
    }

    // Delete an alarm
    override suspend fun deleteAlarm(alarmId: Int) {
        val currentAlarms = alarmsStateFlow.value
        saveAlarms(currentAlarms.filter { it.id != alarmId })
    }

    // Save alarms to DataStore
    private suspend fun saveAlarms(alarms: List<ProAlarm>) {
        val alarmsJson: String = Json.encodeToString(alarms)
        context.dataStore.edit { preferences ->
            preferences[alarmsKey] = alarmsJson
        }
    }


    override fun removeAlarm(alarmId: Int) {
        TODO("Not yet implemented")
    }

    private fun scheduleNotification(proAlarm: ProAlarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", proAlarm.id)
            putExtra("ALARM_MESSAGE", proAlarm.message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            proAlarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Provide a window of 1 minute (60000 milliseconds) around the scheduled time
        val windowLengthMillis = 60000L

        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            proAlarm.timeInMillis,
            windowLengthMillis,
            pendingIntent
        )
    }

    private fun sendTestNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "ALARM_CHANNEL")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Test Notification")
            .setContentText("This is a test notification.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(12345, notification)
    }


    private fun cancelNotification(alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
