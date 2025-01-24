package com.ylabz.basepro.feature.alarm.ui

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.alarm.AlarmReceiver
import com.ylabz.basepro.core.data.repository.alarm.AlarmRepository
import com.ylabz.basepro.core.model.alarm.ProAlarm
import com.ylabz.basepro.core.model.shotime.ShotimeSessionData
import com.ylabz.basepro.core.util.Logging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlarmUiState>(AlarmUiState.Loading)
    val uiState: StateFlow<AlarmUiState> = _uiState

    private val notificationChannelId = "ALARM_CHANNEL"
    private val notificationChannelName = "Alarm Notifications"

    init {
        setupNotificationChannel()
        loadAlarms()
    }

    private fun setupNotificationChannel() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            notificationChannelId,
            notificationChannelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for alarm notifications"
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun setAlarm(proAlarm: ProAlarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_MESSAGE", proAlarm.message)
            putExtra("ALARM_ID", proAlarm.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            proAlarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm with a 1-minute window
        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            proAlarm.timeInMillis,
            60000L,
            pendingIntent
        )

        // Save the alarm to the repository
        viewModelScope.launch {
            try {
                alarmRepository.addAlarm(proAlarm)
                loadAlarms() // Reload alarms to update UI state
            } catch (e: Exception) {
                _uiState.value = AlarmUiState.Error("Failed to save alarm: ${e.message}")
            }
        }
    }

    private fun loadAlarms() {
        _uiState.value = AlarmUiState.Loading
        viewModelScope.launch {
            try {
                alarmRepository.getAlarms().collect { alarms ->
                    val sessionData = alarms.map { alarm ->
                        ShotimeSessionData(shot = alarm.message)
                    }
                    _uiState.value = if (sessionData.isEmpty()) {
                        AlarmUiState.Empty
                    } else {
                        AlarmUiState.Success(sessionData)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AlarmUiState.Error("Failed to load alarms: ${e.message}")
            }
        }
    }

    fun onEvent(event: AlarmEvent) {
        when (event) {
            AlarmEvent.Alarm -> loadAlarms()
        }
    }
}


