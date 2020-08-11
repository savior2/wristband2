package com.zjut.wristband2.activity

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lifesense.ble.OnDeviceReadListener
import com.zjut.wristband2.R
import com.zjut.wristband2.util.DeviceUtil
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.toast
import kotlinx.android.synthetic.main.activity_device_manage.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class DeviceManageActivity : AppCompatActivity() {

    private val address = SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)
        .getString(SpUtil.SpAccount.MAC_ADDRESS, "")!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_manage)
        with(toolbar) {
            navigationIcon = getDrawable(com.zjut.wristband2.R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
        monitorLayout.setOnClickListener {
            monitorManage()
        }
        powerLayout.setOnClickListener {
            readPower()
        }
    }

    private fun monitorManage() {
        AlertDialog.Builder(this)
            .setTitle("打开/关闭心率监测？")
            .setSingleChoiceItems(
                arrayOf("关闭", "打开"), -1
            ) { p0, p1 ->
                if (p1 == 0) {
                    DeviceUtil.stopHeartRateMonitor(address)
                    toast(this, "心率监测已关闭")
                } else {
                    DeviceUtil.startHeartRateMonitor(address)
                    toast(this, "心率监测已开启")
                }
                //p0.dismiss()
            }
            .create().show()
    }

    private fun readPower() {
        DeviceUtil.readPower(address, OnDeviceReadListener { p0, p1, p2, p3 ->
            runOnUiThread {
                AlertDialog.Builder(this)
                    .setTitle("当前剩余电量：$p3%")
                    .setPositiveButton("确定") { _, _ -> }
                    .create().show()
            }
        })
    }
}
