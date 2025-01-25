package com.ylabz.basepro.feature.alarm.ui

import com.ylabz.basepro.core.model.alarm.ProAlarm

sealed class AlarmEvent {
    data class AddAlarm(val proAlarm: ProAlarm) : AlarmEvent() // Pass ProAlarm directly
    object DeleteAll : AlarmEvent() // New event for deleting all alarms
}

