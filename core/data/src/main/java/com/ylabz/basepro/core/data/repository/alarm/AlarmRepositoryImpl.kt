package com.ylabz.basepro.core.data.repository.alarm

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import javax.inject.Inject



class AlarmRepositoryImpl @Inject constructor(
    private val context: Context // or a database/DAO if required
) : AlarmRepository {
    private val alarmList = mutableListOf<Alarm>()

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun addAlarm(alarm: Alarm) {
        alarmList.add(alarm)
        scheduleNotification(alarm)
    }

    fun deleteAlarm(alarmId: Int) {
        alarmList.removeAll { it.id == alarmId }
        cancelNotification(alarmId)
    }

    override fun getAlarms(): List<Alarm> = alarmList
    override fun removeAlarm(alarmId: Int) {
        TODO("Not yet implemented")
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private fun scheduleNotification(alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_MESSAGE", alarm.message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarm.timeInMillis,
            pendingIntent
        )
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

data class Alarm(
    val id: Int,
    val timeInMillis: Long,
    val message: String
)

