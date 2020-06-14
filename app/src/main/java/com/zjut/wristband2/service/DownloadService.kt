package com.zjut.wristband2.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.zjut.wristband2.R
import com.zjut.wristband2.activity.HomeActivity
import com.zjut.wristband2.task.DownloadListener
import com.zjut.wristband2.task.DownloadTask
import java.io.File

class DownloadService : Service() {
    override fun onBind(intent: Intent): IBinder = DownloadBinder()

    inner class DownloadBinder : Binder() {
        fun start(url: String) {
            DownloadTask(object : DownloadListener {
                override fun onSuccess() {
                    val path =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                    val file = File(path, "wristband.apk")
                    val intent = Intent().apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        action = Intent.ACTION_VIEW
                        setDataAndType(
                            Uri.fromFile(file),
                            "application/vnd.android.package-archive"
                        )
                    }
                    startActivity(intent)
                    getNotificationManager().cancel(1)
                }

                override fun onFail() {
                }

                override fun onProgress(p: Int) {
                    if (p < 100) {
                        getNotificationManager().notify(1, getNotification("Downloading...", p))
                    }
                }

            }).execute(url)
            getNotificationManager().notify(1, getNotification("开始下载...", 0))
        }
    }

    private fun getNotificationManager() =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun getNotification(title: String, progress: Int): Notification {
        val intent = Intent(this, HomeActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val builder = NotificationCompat.Builder(this)
            .apply {
                setSmallIcon(R.mipmap.ic_run)
                setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_run))
                setContentTitle(title)
                setContentIntent(pi)
                if (progress > 0) {
                    setContentText("$progress%")
                    setProgress(100, progress, false)
                }
            }
        return builder.build()
    }
}
