package com.ylabz.basepro.ashbike.wear.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WearApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        // Channel for Bike Ride Service
        val bikeServiceChannelName = "Bike Ride Updates"
        val bikeServiceChannelDescription = "Notifications for active bike rides"
        val bikeServiceChannelImportance = NotificationManager.IMPORTANCE_LOW
        val bikeServiceChannel = NotificationChannel(
            BikeForegroundService.NOTIFICATION_CHANNEL_ID, // Using the constant from your service
            bikeServiceChannelName,
            bikeServiceChannelImportance
        ).apply {
            description = bikeServiceChannelDescription
        }

        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(bikeServiceChannel)
    }

}