package com.ylabz.basepro.applications.photodo.ui

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotodoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmRepository: AlarmRepository
) : ViewModel() {
    private val TAG = Logging.getTag(this::class.java)

    // StateFlow for detecting the TI Tag Sensor
    private val _uiState = MutableStateFlow<PhotodoUiState>(PhotodoUiState.Loading)
    val uiState: StateFlow<PhotodoUiState> = _uiState


    private var isBluetoothDialogAlreadyShown = false

    private val notificationChannelId = "SHOT_CHANNEL"
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

        // Provide a window of 1 minute (60000 milliseconds) around the scheduled time
        val windowLengthMillis = 60000L

        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            proAlarm.timeInMillis,
            windowLengthMillis,
            pendingIntent
        )

        // Save the alarm to a repository for persistence
        viewModelScope.launch {
            alarmRepository.addAlarm(proAlarm)
        }
    }



    private fun loadShotimes() {
        _uiState.value = PhotodoUiState.Loading
        viewModelScope.launch {
            try {
                /*val dat : ShotimeSessionData = ShotimeSessionData(
                    shot = "one"
                )*/
                val listDat = emptyList<ShotimeSessionData>()
                _uiState.value = PhotodoUiState.Success(listDat)
            } catch (e: Exception) {
                _uiState.value = PhotodoUiState.Error("Failed to load coffee shops")
            }
        }
    }


    fun onEvent(event: PhotodoEvent) {
        when (event) {
            PhotodoEvent.Photodo -> getShotimes()
        }
    }

    private fun getShotimes() {
        viewModelScope.launch {

        }
    }
}

