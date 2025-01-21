package com.ylabz.basepro.feature.shotime.ui

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.alarm.Alarm
import com.ylabz.basepro.core.data.repository.alarm.AlarmReceiver
import com.ylabz.basepro.core.data.repository.alarm.AlarmRepository
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepository
import com.ylabz.basepro.core.model.ble.ScanState
import com.ylabz.basepro.core.model.shotime.ShotimeSessionData
import com.ylabz.basepro.core.util.Logging
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShotimeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmRepository: AlarmRepository
) : ViewModel() {
    private val TAG = Logging.getTag(this::class.java)

    // StateFlow for detecting the TI Tag Sensor
    private val _uiState = MutableStateFlow<ShotimeUiState>(ShotimeUiState.Loading)
    val uiState: StateFlow<ShotimeUiState> = _uiState


    private var isBluetoothDialogAlreadyShown = false

    private val notificationChannelId = "ALARM_CHANNEL"
    private val notificationChannelName = "Alarm Notifications"

    init {
        // Initialize and set up the notification channel when the ViewModel is created
        setupNotificationChannel()
        loadShotimes() // SF latitude and longitude
    }


    fun setupNotificationChannel() {
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

    // Other ViewModel logic
    fun triggerAlarm() {
        // Logic for setting up and handling alarms
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun setAlarm(alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_MESSAGE", alarm.message)
            putExtra("ALARM_ID", alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.timeInMillis,
            pendingIntent
        )

        // Save the alarm to a repository for persistence
        alarmRepository.addAlarm(alarm)
    }



    private fun loadShotimes() {
        _uiState.value = ShotimeUiState.Loading
        viewModelScope.launch {
            try {
                val dat : ShotimeSessionData = ShotimeSessionData(
                    shot = "one"
                )
                val listDat = listOf(dat)
                _uiState.value = ShotimeUiState.Success(listDat)
            } catch (e: Exception) {
                _uiState.value = ShotimeUiState.Error("Failed to load coffee shops")
            }
        }
    }


    fun onEvent(event: ShotimeEvent) {
        when (event) {
            ShotimeEvent.Shotime -> getShotimes()
        }
    }

    private fun getShotimes() {
        viewModelScope.launch {

        }
    }
}

