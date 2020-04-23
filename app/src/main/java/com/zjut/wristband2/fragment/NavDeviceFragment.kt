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
import com.zjut.wristband2.R
import com.zjut.wristband2.adapter.DeviceAdapter
import com.zjut.wristband2.adapter.DeviceItem
import com.zjut.wristband2.util.DeviceUtil
import com.zjut.wristband2.util.toast
import com.zjut.wristband2.vm.HomeActivityVM
import kotlinx.android.synthetic.main.fragment_nav_device.*

class NavDeviceFragment : Fragment() {

    private lateinit var viewModel: HomeActivityVM

    private val array = arrayListOf<DeviceItem>()
    private lateinit var adapter2: DeviceAdapter

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
        if (viewModel.isConnected) {
            connect()
        } else {
            disconnect()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun connect() {
        recyclerView.visibility = View.GONE
        switch1.isEnabled = false
        deviceTextView.visibility = View.VISIBLE
        disconnect.visibility = View.VISIBLE
        disconnect.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("确定断开连接？")
                .setPositiveButton("确定") { _, _ ->
                    viewModel.isConnected = false
                    DeviceUtil.stopDataReceive()
                    requireActivity().onBackPressed()
                }
                .setNegativeButton("取消") { _, _ -> }
                .create().show()
        }
        deviceTextView.text = "当前连接设备：${viewModel.typeName}[${viewModel.address}]"
    }

    private fun disconnect() {
        recyclerView.visibility = View.VISIBLE
        switch1.isEnabled = true
        deviceTextView.visibility = View.GONE
        disconnect.visibility = View.GONE
        adapter2 = DeviceAdapter(this@NavDeviceFragment.requireContext(), viewModel, array) {
            requireActivity().onBackPressed()
        }
        recyclerView.apply {
            val manager = LinearLayoutManager(requireContext())
            manager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = manager
            this.adapter = adapter2
        }
        switch1.setOnCheckedChangeListener { _, b ->
            if (b) {
                beginScan()
            } else {
                stopScan()
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
            DeviceUtil.startSearch(MySearchCallback())
        }
    }

    private fun isLocationEnabled() = !TextUtils.isEmpty(
        Settings.Secure.getString(
            requireActivity().contentResolver,
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )
    )

    private fun stopScan() {
        progressBar.visibility = View.GONE
        switch1.isChecked = false
        DeviceUtil.stopDataReceive()
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

    companion object {
        private const val ENABLE_BLUETOOTH = 0
        private const val REQUEST_PERMISSION = 1
    }

}
