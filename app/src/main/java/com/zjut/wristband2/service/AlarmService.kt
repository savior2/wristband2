package com.zjut.wristband2.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log

class AlarmService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            Log.e("test", "hello")
        }.start()
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val trigger = SystemClock.elapsedRealtime() + 5000
        val intent2 = Intent(this, AlarmService::class.java)
        val pi = PendingIntent.getService(this, 0, intent2, 0)
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger, pi)
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        fun stopAlarm(context: Context) {
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent2 = Intent(context, AlarmService::class.java)
            val pi = PendingIntent.getService(context, 0, intent2, 0)
            manager.cancel(pi)
        }
    }
}
