package com.zjut.wristband2

import android.app.Application
import android.content.Context
import com.baidu.mapapi.SDKInitializer
import com.mob.MobSDK
import com.zjut.wristband2.util.DeviceUtil
import com.zjut.wristband2.util.RunMode

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        _context = applicationContext

        DeviceUtil.init()
        SDKInitializer.initialize(this)
        MobSDK.init(this)
    }

    companion object {
        private lateinit var _context: Context
        val context get() = _context
        var mode = RunMode.Stop
        var num = 0L
        var heartRate = 0
        var isConnect = false
        var isDevicePage = false
    }
}