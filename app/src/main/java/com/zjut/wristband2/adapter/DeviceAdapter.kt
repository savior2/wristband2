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
import com.zjut.wristband2.MyApplication
import com.zjut.wristband2.R
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.repo.AerobicsHeart
import com.zjut.wristband2.repo.DailyHeart
import com.zjut.wristband2.repo.MyDatabase
import com.zjut.wristband2.repo.SportsHeart
import com.zjut.wristband2.task.DeviceConnectTask
import com.zjut.wristband2.task.TaskListener
import com.zjut.wristband2.util.DeviceUtil
import com.zjut.wristband2.util.RunMode
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.TimeTransfer
import com.zjut.wristband2.vm.HomeActivityVM
import kotlinx.android.synthetic.main.cell_device.view.*
import java.util.*


class DeviceAdapter(
    private val context: Context,
    private val viewModel: HomeActivityVM,
    private val array: List<DeviceItem>,
    private val listener: ConnectListener
) :
    RecyclerView.Adapter<DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_device, parent, false)
        return DeviceViewHolder(view).apply {
            view.setOnClickListener {
                val address = array[this.adapterPosition].address
                AlertDialog.Builder(context)
                    .setTitle("确定连接手环$address？")
                    .setPositiveButton("确定") { _, _ ->
                        connect(array[this.adapterPosition])
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

    private fun connect(item: DeviceItem) {
        viewModel.address = item.address
        viewModel.typeName = item.typeName
        viewModel.type = item.type
        DeviceUtil.stopSearch()
        DeviceUtil.startDataReceive(
            item.type,
            item.address,
            MyDataCallback(viewModel, listener)
        )
        listener.startConnect(item)
    }

    interface ConnectListener {
        fun startConnect(item: DeviceItem)
        fun finishConnect()
    }

}


class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

data class DeviceItem(val type: String, val address: String, val typeName: String)


const val TAG = "MyDataCallback"

class MyDataCallback(
    private val viewModel: HomeActivityVM,
    private val listener: DeviceAdapter.ConnectListener?
) :
    ReceiveDataCallback() {
    override fun onDeviceConnectStateChange(p0: DeviceConnectState?, p1: String?) {
        super.onDeviceConnectStateChange(p0, p1)
        Log.e(TAG, "device connect status: $p0, $p1")
        when (p0) {
            DeviceConnectState.CONNECTED_SUCCESS -> {
                viewModel.isConnected = true
                MyApplication.isConnect = true
                with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).edit()) {
                    putString(SpUtil.SpAccount.MAC_ADDRESS, viewModel.address)
                    putString(SpUtil.SpAccount.MAC_TYPE, viewModel.type)
                    putString(SpUtil.SpAccount.MAC_NAME, viewModel.typeName)
                    apply()
                }
                DeviceConnectTask(object : TaskListener {
                    override fun onStart() {}

                    override fun onSuccess() {
                        listener?.finishConnect()
                    }

                    override fun onFail(code: WCode) {}

                }).execute()
            }
            DeviceConnectState.DISCONNECTED -> {
                viewModel.isConnected = false
                MyApplication.isConnect = false
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
                val p = p0[p0.size - 1] as PedometerData
                val d = TimeTransfer.utc2Date(p.utc)
                val d1 = Date()
                if (d.year == d1.year && d.month == d1.month && d.date == d1.date) {
                    with(SpUtil.getSp(SpUtil.SpStatistics.FILE_NAME).edit()) {
                        putInt(SpUtil.SpStatistics.STEP, p.walkSteps)
                        putLong(SpUtil.SpStatistics.UTC, p.utc)
                        apply()
                    }
                    viewModel.step = p.walkSteps
                }

            }
            is PedometerHeartRateData -> {
                val array = arrayListOf<DailyHeart>()
                for (i in 0 until p0.heartRates.size) {
                    array.add(
                        DailyHeart(
                            p0.heartRates[i] as Int,
                            p0.utc + i * 300,
                            viewModel.address
                        )
                    )
                }
                Thread {
                    MyDatabase.instance.getDailyHeartDao().insert(*array.toTypedArray())
                }.start()
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
            MyApplication.heartRate = p1.heartRates[0] as Int
            when (MyApplication.mode) {
                RunMode.Aerobics -> {
                    Thread {
                        MyDatabase.instance.getAerobicsHeartDao().insert(
                            AerobicsHeart(
                                MyApplication.num,
                                p1.heartRates[0] as Int,
                                p1.utc
                            )
                        )
                    }.start()
                }
                RunMode.Indoor, RunMode.Outdoor -> {
                    Thread {
                        MyDatabase.instance.getSportsHeartDao().insert(
                            SportsHeart(
                                MyApplication.num,
                                p1.heartRates[0] as Int,
                                p1.utc
                            )
                        )
                    }.start()
                }
            }
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