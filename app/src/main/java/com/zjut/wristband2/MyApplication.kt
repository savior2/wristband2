package com.zjut.wristband2

import android.app.Application
import android.content.Context
import com.baidu.mapapi.SDKInitializer
import com.lifesense.ble.LsBleManager
import com.zjut.wristband2.util.DeviceUtil
import com.zjut.wristband2.util.RunMode

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        _context = applicationContext

        DeviceUtil.init()
        SDKInitializer.initialize(this)
    }

    companion object {
        private lateinit var _context: Context
        val context get() = _context
        var mode = RunMode.Normal
        var num = 0
        var heartRate = 0
    }
}