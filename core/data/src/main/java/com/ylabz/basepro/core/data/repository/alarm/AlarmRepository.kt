package com.ylabz.basepro.core.data.repository.alarm

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import kotlin.jvm.java

interface AlarmRepository {
    fun addAlarm(alarm: Alarm)
    fun getAlarms(): List<Alarm>
    fun removeAlarm(alarmId: Int)
}

