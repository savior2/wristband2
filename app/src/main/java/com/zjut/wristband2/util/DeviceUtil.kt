package com.zjut.wristband2.util

import com.lifesense.ble.LsBleManager
import com.lifesense.ble.OnSettingListener
import com.lifesense.ble.ReceiveDataCallback
import com.lifesense.ble.SearchCallback
import com.lifesense.ble.bean.LsDeviceInfo
import com.lifesense.ble.bean.constant.BroadcastType
import com.lifesense.ble.bean.constant.DeviceType
import com.zjut.wristband2.MyApplication

object DeviceUtil {
    private val instance by lazy {
        LsBleManager.getInstance()
    }

    fun stopSearch() = instance.stopSearch()

    fun startSearch(callback: SearchCallback) = instance.searchLsDevice(
        callback,
        listOf(DeviceType.PEDOMETER),
        BroadcastType.ALL
    )

    fun stopDataReceive() = instance.stopDataReceiveService()

    fun startDataReceive(type: String, address: String, callback: ReceiveDataCallback) {
        val device = LsDeviceInfo().apply {
            deviceType = type
            macAddress = address
        }
        with(instance) {
            stopDataReceiveService()
            setMeasureDevice(null)
            addMeasureDevice(device)
            startDataReceiveService(callback)
        }
    }

    fun startRealtime(address: String) =
        instance.setRealtimeHeartRateSyncState(address, true, object :
            OnSettingListener() {
            override fun onSuccess(p0: String?) {
                super.onSuccess(p0)
            }

            override fun onFailure(p0: Int) {
                super.onFailure(p0)
            }
        })


    fun stopRealTime(address: String) =
        instance.setRealtimeHeartRateSyncState(address, false, object :
            OnSettingListener() {
            override fun onSuccess(p0: String?) {
                super.onSuccess(p0)
            }

            override fun onFailure(p0: Int) {
                super.onFailure(p0)
            }
        })

    fun init() {
        //init LSBluetoothManager
        LsBleManager.getInstance().initialize(MyApplication.context)

        //register bluetooth broadcast receiver
        LsBleManager.getInstance().registerBluetoothBroadcastReceiver(MyApplication.context)

        //register message service
        LsBleManager.getInstance().registerMessageService()
    }
}