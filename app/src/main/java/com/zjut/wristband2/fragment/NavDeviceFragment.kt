package com.zjut.wristband2.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lifesense.ble.SearchCallback
import com.lifesense.ble.bean.LsDeviceInfo
import com.zjut.wristband2.MyApplication
import com.zjut.wristband2.R
import com.zjut.wristband2.adapter.DeviceAdapter
import com.zjut.wristband2.adapter.DeviceItem
import com.zjut.wristband2.adapter.MyDataCallback
import com.zjut.wristband2.util.DeviceUtil
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.toast
import com.zjut.wristband2.vm.HomeActivityVM
import kotlinx.android.synthetic.main.fragment_nav_device.*
import kotlinx.android.synthetic.main.module_connect.view.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class NavDeviceFragment : Fragment() {

    private lateinit var viewModel: HomeActivityVM

    private val array = arrayListOf<DeviceItem>()
    private lateinit var adapter2: DeviceAdapter

    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nav_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            SavedStateViewModelFactory(requireActivity().application, requireActivity())
        )[HomeActivityVM::class.java]

        viewModel.isBind = viewModel.address != ""

        if (viewModel.isBind) {
            deviceTextView.visibility = View.VISIBLE
            deviceTextView.text = "当前绑定设备：${viewModel.typeName}[${viewModel.address}]"
            unBindButton.visibility = View.VISIBLE
            unBindButton.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("确定解绑当前设备？")
                    .setPositiveButton("确定") { _, _ ->
                        viewModel.isBind = false
                        viewModel.isConnected = false
                        DeviceUtil.stopDataReceive()
                        viewModel.address = ""
                        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).edit()) {
                            putString(SpUtil.SpAccount.MAC_ADDRESS, "")
                            apply()
                        }
                        toast(this@NavDeviceFragment.requireContext(), "解绑成功！")
                        disconnect()
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .create().show()
            }

        } else {
            deviceTextView.visibility = View.INVISIBLE
            unBindButton.visibility = View.INVISIBLE
        }

        if (viewModel.isConnected) {
            connect()
        } else {
            disconnect()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun connect() {
        if (viewModel.isBind) {
            deviceTextView.visibility = View.VISIBLE
            deviceTextView.text = "当前绑定设备：${viewModel.typeName}[${viewModel.address}]"
            unBindButton.visibility = View.VISIBLE
            unBindButton.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("确定解绑当前设备？")
                    .setPositiveButton("确定") { _, _ ->
                        viewModel.isBind = false
                        viewModel.isConnected = false
                        DeviceUtil.stopDataReceive()
                        viewModel.address = ""
                        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).edit()) {
                            putString(SpUtil.SpAccount.MAC_ADDRESS, "")
                            apply()
                        }
                        toast(this@NavDeviceFragment.requireContext(), "解绑成功！")
                        disconnect()
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .create().show()
            }

        } else {
            deviceTextView.visibility = View.INVISIBLE
            unBindButton.visibility = View.INVISIBLE
        }

        stopScan()
        recyclerView.visibility = View.INVISIBLE
        switch1.isEnabled = false
        connectStatus.text = "已连接"
        connectTip.visibility = View.VISIBLE
        connectTip.setImageDrawable(resources.getDrawable(R.drawable.ic_connect))
        disconnect.visibility = View.VISIBLE
        disconnect.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("确定断开连接？")
                .setPositiveButton("确定") { _, _ ->
                    viewModel.isConnected = false
                    DeviceUtil.stopDataReceive()
                    toast(this@NavDeviceFragment.requireContext(), "已断开连接！")
                    disconnect()
                }
                .setNegativeButton("取消") { _, _ -> }
                .create().show()
        }
    }

    private fun disconnect() {
        switch1.isEnabled = true
        disconnect.visibility = View.GONE
        switch1.setOnCheckedChangeListener { _, b ->
            if (b) {
                beginScan()
            } else {
                stopScan()
            }
        }
        if (viewModel.isBind) {
            recyclerView.visibility = View.INVISIBLE
            connectStatus.text = "未连接"
            connectTip.visibility = View.VISIBLE
            connectTip.setImageDrawable(resources.getDrawable(R.drawable.ic_disconnect))

        } else {
            recyclerView.visibility = View.VISIBLE
            connectStatus.text = "扫描附近手环"
            connectTip.visibility = View.GONE
            deviceTextView.visibility = View.INVISIBLE
            unBindButton.visibility = View.INVISIBLE

            adapter2 = DeviceAdapter(
                this@NavDeviceFragment.requireContext(),
                viewModel,
                array,
                object : DeviceAdapter.ConnectListener {
                    @SuppressLint("SetTextI18n")
                    override fun startConnect(item: DeviceItem) {
                        MyApplication.isDevicePage = true
                        array.clear()
                        adapter2.notifyDataSetChanged()
                        val dialogView =
                            LayoutInflater.from(requireContext())
                                .inflate(R.layout.module_connect, null)
                        dialogView.onConnectText.text =
                            "正在连接...\n${item.typeName}[${item.address}]\n请勿切换页面！！！"
                        dialog = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .setCancelable(false)
                            .create()
                        dialog.show()
                        viewModel.isConnecting = true
                        Thread {
                            Thread.sleep(10000)
                            if (viewModel.isConnecting) {
                                viewModel.isConnecting = false
                                this@NavDeviceFragment.activity?.runOnUiThread {
                                    dialog.dismiss()
                                    DeviceUtil.stopDataReceive()
                                    toast(this@NavDeviceFragment.requireContext(), "连接失败！")
                                    progressBar.visibility = View.INVISIBLE
                                    switch1.isChecked = false
                                    beginScan()
                                }
                            }
                        }.start()
                    }

                    override fun finishConnect() {
                        dialog.dismiss()
                        toast(this@NavDeviceFragment.requireContext(), "连接成功！")
                        viewModel.isBind = true
                        viewModel.isConnecting = false
                        connect()
                    }
                })
            recyclerView.apply {
                val manager = LinearLayoutManager(requireContext())
                manager.orientation = LinearLayoutManager.VERTICAL
                layoutManager = manager
                this.adapter = adapter2
            }

        }
    }


    private fun beginScan() {
        progressBar.visibility = View.VISIBLE
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, ENABLE_BLUETOOTH)
        } else {
            getLocationPermission()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH -> {
                if (resultCode == Activity.RESULT_OK) {
                    getLocationPermission()
                } else {
                    toast(this.requireContext(), "请先打开蓝牙！")
                    stopScan()
                }
            }
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION
            )
        } else {
            openLocationService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openLocationService()
                } else {
                    toast(this.requireContext(), "请先授权位置权限！")
                    stopScan()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openLocationService() {
        if (!isLocationEnabled()) {
            AlertDialog.Builder(activity)
                .setTitle("请先打开位置服务！")
                .setPositiveButton("确定") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("取消") { _, _ -> }
                .create().show()
            stopScan()
        } else {
            if (viewModel.isBind) {
                MyApplication.isDevicePage = true
                DeviceUtil.stopSearch()
                val dialogView =
                    LayoutInflater.from(requireContext())
                        .inflate(R.layout.module_connect, null)
                dialogView.onConnectText.text =
                    "正在连接...\n${viewModel.typeName}[${viewModel.address}]\n请勿切换页面！！！"
                dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setCancelable(false)
                    .create()
                dialog.show()
                viewModel.isConnecting = true
                Thread {
                    Thread.sleep(10000)
                    if (viewModel.isConnecting) {
                        viewModel.isConnecting = false
                        this@NavDeviceFragment.activity?.runOnUiThread {
                            dialog.dismiss()
                            DeviceUtil.stopDataReceive()
                            progressBar.visibility = View.INVISIBLE
                            switch1.isChecked = false
                            toast(this@NavDeviceFragment.requireContext(), "连接失败！")
                        }
                    }
                }.start()
                DeviceUtil.startDataReceive(
                    viewModel.type,
                    viewModel.address,
                    MyDataCallback(viewModel, object : DeviceAdapter.ConnectListener {
                        override fun startConnect(item: DeviceItem) {
                        }

                        override fun finishConnect() {
                            dialog.dismiss()
                            toast(requireActivity(), "连接成功！")
                            viewModel.isBind = true
                            viewModel.isConnecting = false
                            connect()
                        }

                    })
                )
            } else {
                array.clear()
                adapter2.notifyDataSetChanged()
                DeviceUtil.startSearch(MySearchCallback())
            }
        }
    }

    private fun isLocationEnabled() = !TextUtils.isEmpty(
        Settings.Secure.getString(
            requireActivity().contentResolver,
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )
    )

    private fun stopScan() {
        progressBar.visibility = View.INVISIBLE
        switch1.isChecked = false
        DeviceUtil.stopSearch()
    }

    private inner class MySearchCallback : SearchCallback() {
        override fun onSearchResults(lsDeviceInfo: LsDeviceInfo) {
            //Log.e("My", lsDeviceInfo.toString())
            val item = DeviceItem(
                lsDeviceInfo.deviceType,
                lsDeviceInfo.macAddress,
                lsDeviceInfo.deviceName
            )
            if (!array.contains(item)) {
                array.add(item)
                this@NavDeviceFragment.requireActivity().runOnUiThread {
                    adapter2.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.isDevicePage = false
    }

    companion object {
        private const val ENABLE_BLUETOOTH = 0
        private const val REQUEST_PERMISSION = 1
    }

}
