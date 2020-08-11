package com.zjut.wristband2.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class NotificationUtil : ContextWrapper {
    private var manager: NotificationManager? = null

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(base: Context) : super(base) {
        createChannels()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        with(
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        ) {
            enableLights(true)
            enableVibration(true)
            lightColor = Color.GREEN
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            getManager().createNotificationChannel(this)
        }
    }


    private fun getManager(): NotificationManager {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAndroidChannelNotification(title: String, body: String): Notification.Builder =
        Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.stat_notify_more)
            .setAutoCancel(true)

    companion object {
        private const val CHANNEL_ID = "com.zjut.wristband"
        private const val CHANNEL_NAME = "android channel"
    }

}