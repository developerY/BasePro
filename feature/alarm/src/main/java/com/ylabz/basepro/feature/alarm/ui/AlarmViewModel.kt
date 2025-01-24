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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

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

    // Set up the notification channel
    private fun setupNotificationChannel() {
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

    // Load alarms into UI state
    private fun loadAlarms() {
        _uiState.value = AlarmUiState.Loading
        viewModelScope.launch {
            try {
                val alarms = alarmRepository.getAlarms().firstOrNull() ?: emptyList()
                if (alarms.isEmpty()) {
                    _uiState.value = AlarmUiState.Empty
                } else {
                    val data = alarms.map { alarm ->
                        ShotimeSessionData(shot = alarm.message) // Map alarms to ShotimeSessionData
                    }
                    _uiState.value = AlarmUiState.Success(data)
                }
            } catch (e: Exception) {
                _uiState.value = AlarmUiState.Error("Failed to load alarms: ${e.message}")
            }
        }
    }

    // Handle events
    fun onEvent(event: AlarmEvent) {
        when (event) {
            AlarmEvent.AddAlarm -> addAlarm()
            AlarmEvent.DeleteAll -> deleteAllAlarms()
        }
    }

    // Add a new alarm
    private fun addAlarm() {
        val currentTime = System.currentTimeMillis() + 1000 // 1 second later
        val proAlarm = ProAlarm(
            id = Random.nextInt(),
            timeInMillis = currentTime,
            message = "Test Alarm"
        )

        viewModelScope.launch {
            alarmRepository.addAlarm(proAlarm)
            loadAlarms() // Refresh UI state
        }
    }

    // Delete all alarms
    private fun deleteAllAlarms() {
        viewModelScope.launch {
            try {
                alarmRepository.clearAllAlarms()
                loadAlarms() // Refresh UI state
            } catch (e: Exception) {
                _uiState.value = AlarmUiState.Error("Failed to delete all alarms: ${e.message}")
            }
        }
    }
}
