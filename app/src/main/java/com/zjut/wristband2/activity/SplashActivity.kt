package com.zjut.wristband2.activity

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.zjut.wristband2.R
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.util.LogUtil
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.WebUtil

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        login()
    }

    private fun login() {
        var sid = ""
        var password = ""
        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)) {
            sid = getString(SpUtil.SpAccount.SID, "")!!
            password = getString(SpUtil.SpAccount.PASSWORD, "")!!
        }
        Thread {
            Thread.sleep(1000)
            if (TextUtils.isEmpty(sid) || TextUtils.isEmpty(password)) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                val code = WebUtil.login(sid, password)
                if (code != WCode.OK) {
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this, HomeActivity::class.java))
                }

            }
            finish()
        }.start()
    }

}
