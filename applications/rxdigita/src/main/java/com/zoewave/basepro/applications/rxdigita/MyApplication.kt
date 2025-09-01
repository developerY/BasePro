package com.zoewave.basepro.applications.rxdigita

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // createNotificationChannels()
    }
}
