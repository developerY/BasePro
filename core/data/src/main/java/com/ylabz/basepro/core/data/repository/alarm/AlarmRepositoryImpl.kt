package com.ylabz.basepro.core.data.repository.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ylabz.basepro.core.model.alarm.ProAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Date
import javax.inject.Inject


class AlarmRepositoryImpl @Inject constructor(
    private val context: Context
) : AlarmRepository {
    private val alarmsStateFlow = MutableStateFlow<List<ProAlarm>>(emptyList())

    val Context.dataStore by preferencesDataStore(name = "alarms_datastore")
    private val alarmsKey = stringPreferencesKey("alarms_key")

    private val notificationChannelId = "ALARM_CHANNEL"
    private val notificationChannelName = "Alarm Notifications"

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

    // Set up the notification channel
    override fun setupNotificationChannel() {
        val channel = NotificationChannel(
            notificationChannelId,
            notificationChannelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for alarm notifications"
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override suspend fun clearAllAlarms() {
        context.dataStore.edit { preferences ->
            preferences.remove(alarmsKey) // Clear all alarms from DataStore
        }
    }

    override fun getAlarms(): Flow<List<ProAlarm>> = alarmsStateFlow

    // Add a new alarm
    override suspend fun addAlarm(alarm: ProAlarm) {
        val currentAlarms = alarmsStateFlow.value
        saveAlarms(currentAlarms + alarm)
        if (alarm.isEnabled) {
            scheduleNotification(alarm)
        }
    }

    // Delete an alarm
    override suspend fun deleteAlarm(alarmId: Int) {
        val currentAlarms = alarmsStateFlow.value
        val updatedAlarms = currentAlarms.filter { it.id != alarmId }
        saveAlarms(updatedAlarms)

        // Cancel the alarm if it exists
        cancelNotification(alarmId)
    }

    // Save alarms to DataStore
    override suspend fun saveAlarms(alarms: List<ProAlarm>) {
        val alarmsJson: String = Json.encodeToString(alarms)
        context.dataStore.edit { preferences ->
            preferences[alarmsKey] = alarmsJson
        }

        // Schedule notifications for all enabled alarms
        alarms.filter { it.isEnabled }.forEach { scheduleNotification(it) }
    }


    override suspend fun removeAlarm(alarmId: Int) {
        deleteAlarm(alarmId)
    }

    override suspend fun toggleAlarm(alarmId: Int, isEnabled: Boolean) {
        val currentAlarms = alarmsStateFlow.value
        val updatedAlarms = currentAlarms.map { alarm ->
            if (alarm.id == alarmId) {
                alarm.copy(isEnabled = isEnabled).also {
                    if (isEnabled) {
                        scheduleNotification(it)
                    } else {
                        cancelNotification(alarmId)
                    }
                }
            } else {
                alarm
            }
        }
        saveAlarms(updatedAlarms)
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

        // Provide a window of 1 minute 1(0000 milliseconds) around the scheduled time
        val windowLengthMillis = 10000L

        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            proAlarm.timeInMillis,
            windowLengthMillis,
            pendingIntent
        )
    }

    override fun logAlarms() {
        val currentAlarms = alarmsStateFlow.value
        Log.d("AlarmRepository", "Logging Alarms. Total: ${currentAlarms.size}")

        currentAlarms.forEach { alarm ->
            val isScheduled = isAlarmScheduled(alarm)
            Log.d(
                "AlarmRepository",
                "Alarm ID: ${alarm.id}, Time: ${Date(alarm.timeInMillis)}, Message: ${alarm.message}, Scheduled: $isScheduled"
            )
        }

        if (currentAlarms.isEmpty()) {
            Log.d("AlarmRepository", "No alarms found in the local list.")
        }
    }

    private fun isAlarmScheduled(proAlarm: ProAlarm): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", proAlarm.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            proAlarm.id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent != null
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
