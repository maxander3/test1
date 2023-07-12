package com.kz.planning.app

import android.app.Application
import android.content.Intent
import com.onesignal.OneSignal
import io.paperdb.Paper

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Paper.init(applicationContext)
        OneSignal.setNotificationOpenedHandler {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}