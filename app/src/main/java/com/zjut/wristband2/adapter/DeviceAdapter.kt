package com.zjut.wristband2.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lifesense.ble.ReceiveDataCallback
import com.lifesense.ble.bean.*
import com.lifesense.ble.bean.constant.DeviceConnectState
import com.lifesense.ble.bean.constant.PacketProfile
import com.lifesense.ble.bean.constant.PedometerSportsType
import com.zjut.wristband2.R
import com.zjut.wristband2.util.DeviceUtil
import com.zjut.wristband2.vm.HomeActivityVM
import kotlinx.android.synthetic.main.cell_device.view.*


class DeviceAdapter(
    private val context: Context,
    private val viewModel: HomeActivityVM,
    private val array: List<DeviceItem>,
    private val listener: () -> Unit
) :
    RecyclerView.Adapter<DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_device, parent, false)
        return DeviceViewHolder(view).apply {
            view.setOnClickListener {
                val type = array[this.adapterPosition].type
                val address = array[this.adapterPosition].address
                val typeName = array[this.adapterPosition].typeName
                AlertDialog.Builder(context)
                    .setTitle("确定连接手环$address？")
                    .setPositiveButton("确定") { _, _ ->
                        connect(type, address, typeName)
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .create().show()
            }
        }
    }

    override fun getItemCount() = array.size

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        with(holder.itemView) {
            typeTextView.text = array[position].typeName
            addressTextView.text = array[position].address
        }
    }

    private fun connect(type: String, address: String, typeName: String) {
        viewModel.address = address
        viewModel.typeName = typeName
        viewModel.isConnected = true
        DeviceUtil.stopSearch()
        DeviceUtil.startDataReceive(type, address, MyDataCallback(viewModel))
        listener()
    }

}


class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

data class DeviceItem(val type: String, val address: String, val typeName: String)


const val TAG = "MyDataCallback"

class MyDataCallback(private val viewModel: HomeActivityVM) : ReceiveDataCallback() {
    override fun onDeviceConnectStateChange(p0: DeviceConnectState?, p1: String?) {
        super.onDeviceConnectStateChange(p0, p1)
        Log.e(TAG, "device connect status: $p0")
        when (p0) {
            DeviceConnectState.CONNECTED_SUCCESS -> {
                viewModel.isConnected = true
            }
            DeviceConnectState.DISCONNECTED -> {
                viewModel.isConnected = false
            }
            else -> {

            }
        }
    }

    override fun onReceivePedometerMeasureData(p0: Any?, p1: PacketProfile?, p2: String?) {
        super.onReceivePedometerMeasureData(p0, p1, p2)
        Log.e(TAG, "onReceivePedometerMeasureData: $p0")
        when (p0) {
            //list<PedometerData>
            is List<*> -> {
                val stat = p0[p0.size - 1] as PedometerData
            }
            is PedometerHeartRateData -> {
                for (i in 0 until p0.heartRates.size) {

                }
            }
            is PedometerSleepData -> {

            }
            is PedometerRunningStatus -> {
                Log.e(TAG, "run: ${p0.maxHeartRate}")
            }
        }
    }

    override fun onReceiveRealtimeMeasureData(p0: String?, p1: Any?) {
        super.onReceiveRealtimeMeasureData(p0, p1)
        if (p1 is PedometerHeartRateData) {
            Log.e(TAG, "real data: $p0, ${p1.heartRates}")
        }
    }


    override fun onReceivePedometerData(p0: PedometerData?) {
        super.onReceivePedometerData(p0)
        Log.e(TAG, "onReceivePedometerData: $p0")
    }

    override fun onPedometerSportsModeNotify(p0: String?, p1: SportNotify?) {
        super.onPedometerSportsModeNotify(p0, p1)
        Log.e(TAG, "onSportsModeChange: mac_address: $p0, ${p1?.toString()}")
        when (p1?.sportsType ?: return) {
            PedometerSportsType.RUNNING -> {
                Log.e(TAG, "start running")
                /*val intent =
                    Intent(this@DeviceConnectFragment.activity, RunningActivity::class.java)
                intent.putExtra(BroadType.EXTRA_RUNNING, "running")
                startActivity(intent)*/
            }
        }
    }
}