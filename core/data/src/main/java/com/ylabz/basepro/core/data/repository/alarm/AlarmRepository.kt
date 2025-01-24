package com.ylabz.basepro.core.data.repository.alarm

import com.ylabz.basepro.core.model.alarm.ProAlarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun addAlarm(proAlarm: ProAlarm)
    suspend fun deleteAlarm(alarmId: Int)
    suspend fun clearAllAlarms()
    fun getAlarms(): Flow<List<ProAlarm>>
    fun removeAlarm(alarmId: Int)
}

