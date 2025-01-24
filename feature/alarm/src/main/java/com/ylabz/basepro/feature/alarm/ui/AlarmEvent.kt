package com.ylabz.basepro.feature.alarm.ui

sealed interface AlarmEvent {
    object AddAlarm : AlarmEvent
    object DeleteAll : AlarmEvent // New event for deleting all alarms
}

