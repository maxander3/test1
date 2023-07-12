package com.kz.planning.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.onesignal.OneSignal

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
        OneSignal.initWithContext(applicationContext)
        OneSignal.disablePush(false)
        OneSignal.setAppId("ebdc4820-85ab-44ef-b5c4-662e38856788")
    }
}