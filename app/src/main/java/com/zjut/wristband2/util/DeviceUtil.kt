package com.zjut.wristband2.util

import com.lifesense.ble.*
import com.lifesense.ble.bean.LsDeviceInfo
import com.lifesense.ble.bean.constant.BroadcastType
import com.lifesense.ble.bean.constant.DeviceType
import com.lifesense.ble.bean.constant.HeartRateDetectionMode
import com.zjut.wristband2.MyApplication

/**
 * @author qpf
 * @date 2020-8
 * @description Lexin wristband util
 */
object DeviceUtil {
    /**
     * singleton instance
     */
    private val instance by lazy {
        LsBleManager.getInstance()
    }

    /**
     * stop blue tooth search
     */
    fun stopSearch() = instance.stopSearch()

    /**
     * start search device nearby using bluetooth
     * @param callback
     */
    fun startSearch(callback: SearchCallback) = instance.searchLsDevice(
        callback,
        listOf(DeviceType.PEDOMETER),
        BroadcastType.ALL
    )

    /**
     * stop data receive from device
     */
    fun stopDataReceive() = instance.stopDataReceiveService()

    /**
     * start receive data from device
     * @param type the device type
     * @param address the device mac address
     * @param callback
     */
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

    /**
     * start receive realtime heart rate from device
     * @param address device mac address
     */
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


    /**
     * stop receive realtime heart rate
     * @param address
     */
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


    /**
     * open the heart rate monitor switch
     * @param address
     */
    fun startHeartRateMonitor(address: String) {
        instance.updatePedometerHeartDetectionMode(
            address,
            HeartRateDetectionMode.values()[1],
            object :
                OnSettingListener() {
            })
    }

    /**
     * close the heart rate monitor switch
     * @param address
     */
    fun stopHeartRateMonitor(address: String) {
        instance.updatePedometerHeartDetectionMode(
            address,
            HeartRateDetectionMode.values()[0],
            object :
                OnSettingListener() {
            })
    }

    /**
     * read the device power
     * @param address
     * @param listener the callback
     */
    fun readPower(address: String, listener: OnDeviceReadListener) {
        instance.readDeviceVoltage(address, listener)
    }

    fun init() {
        //init LSBluetoothManager
        LsBleManager.getInstance().initialize(MyApplication.context)

        //register bluetooth broadcast receiver
        LsBleManager.getInstance().registerBluetoothBroadcastReceiver(MyApplication.context)

        //register message service
        LsBleManager.getInstance().registerMessageService()
    }
}