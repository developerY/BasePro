package com.zoewave.basepro.applications.rxdigita

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService // Added import
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // createNotificationChannels()
    }
}
