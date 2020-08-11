package com.zjut.wristband2.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.audiofx.BassBoost
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.zjut.wristband2.R
import com.zjut.wristband2.task.DownloadListener
import com.zjut.wristband2.task.DownloadTask
import java.io.File

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class DownloadService : Service() {
    override fun onBind(intent: Intent): IBinder = DownloadBinder()

    inner class DownloadBinder : Binder() {
        fun start(url: String) {
            DownloadTask(object : DownloadListener {
                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                val file = File(path, "wristband.apk")
                val intent = Intent(Intent.ACTION_VIEW)
                override fun onSuccess() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.flags =
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        val uri = FileProvider.getUriForFile(
                            this@DownloadService,
                            "com.zjut.wristband2.fileprovider",
                            file
                        )
                        intent.setDataAndType(
                            uri,
                            "application/vnd.android.package-archive"
                        )
                    } else {
                        intent.apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            setDataAndType(
                                Uri.fromFile(file),
                                "application/vnd.android.package-archive"
                            )
                        }
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
        var notification: Notification? = null
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts(
                "package",
                packageName, null
            )
        }
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("wb", "WB", NotificationManager.IMPORTANCE_HIGH)
            getNotificationManager().createNotificationChannel(channel)
            notification = Notification.Builder(this, channel.id)
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
                .build()

        } else {
            notification = NotificationCompat.Builder(this)
                .apply {
                    setSmallIcon(R.mipmap.ic_run)
                    setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_run))
                    setContentTitle(title)
                    setContentIntent(pi)
                    if (progress > 0) {
                        setContentText("$progress%")
                        setProgress(100, progress, false)
                    }
                }.build()
        }
        return notification!!
    }
}
