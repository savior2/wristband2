package com.zjut.wristband2.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband2.R
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.WebUtil
import com.zjut.wristband2.util.isNetworkConnected
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        imageView.playAnimation()
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
            Thread.sleep(2000)
            if (TextUtils.isEmpty(sid) || TextUtils.isEmpty(password)) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                if (!isNetworkConnected()) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    val code = WebUtil.login(sid, password)
                    if (code != WCode.OK) {
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                }
            }
            finish()
        }.start()
    }

}
