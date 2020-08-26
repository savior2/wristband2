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
 * @description define some global variables and init some frameworks
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        _context = applicationContext

        //init Lexin device
        DeviceUtil.init()
        //init Baidu map service
        SDKInitializer.initialize(this)
        //init Mobsdk that use for sharing
        MobSDK.init(this)
    }

    companion object {
        private lateinit var _context: Context
        val context get() = _context
        var mode = RunMode.Stop

        /**
         *the id of sports summary
         */
        var num = 0L

        /**
         *realtime heart rate
         */
        var heartRate = 0

        /**
         * the status of device
         */
        var isConnect = false

        /**
         *solve the bluetooth disconnect problem
         */
        var isDevicePage = false
    }
}