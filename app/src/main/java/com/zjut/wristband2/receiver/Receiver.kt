package com.zjut.wristband2.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.WebUtil
import com.zjut.wristband2.util.isNetworkConnected

/**
 * @author qpf
 * @date 2020-8
 * @description 无网络
 */
class NetworkReceiver(private val listener: NetworkListener) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (isNetworkConnected()) {
            Log.e("test","network changed")
            var sid = ""
            var password = ""
            with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)) {
                sid = getString(SpUtil.SpAccount.SID, "")!!
                password = getString(SpUtil.SpAccount.PASSWORD, "")!!
            }
            Thread {
                val code = WebUtil.login(sid, password)
                if (code != WCode.OK) {
                    listener.doWork()
                }
            }.start()
        }
    }

    interface NetworkListener {
        fun doWork()
    }
}

