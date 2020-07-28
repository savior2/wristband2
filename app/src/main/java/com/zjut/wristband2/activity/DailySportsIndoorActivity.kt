package com.zjut.wristband2.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zjut.wristband2.MyApplication
import com.zjut.wristband2.R
import com.zjut.wristband2.databinding.ActivityDailySportsIndoorBinding
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.task.*
import com.zjut.wristband2.util.DeviceUtil
import com.zjut.wristband2.util.RunMode
import com.zjut.wristband2.util.toast
import com.zjut.wristband2.vm.DailySportsInActivityVM
import kotlinx.android.synthetic.main.activity_daily_sports_indoor.*

class DailySportsIndoorActivity : AppCompatActivity() {

    private lateinit var viewModel: DailySportsInActivityVM
    private lateinit var binding: ActivityDailySportsIndoorBinding

    private lateinit var mLocationManager: LocationManager

    private lateinit var manager: AlarmManager
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_sports_indoor)
        viewModel = ViewModelProvider(this)[DailySportsInActivityVM::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)
        with(binding.toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        initView()
        initControl()
    }

    private fun initView() {
        val array = arrayListOf("跑步机", "功率自行车", "举重", "羽毛球", "排球", "网球", "乒乓球", "跳绳")
        val adapter2 = ArrayAdapter(this, R.layout.module_spinner_item, array)
        adapter2.setDropDownViewResource(R.layout.module_spinner_dropdown_item)
        with(binding.spinner) {
            adapter = adapter2
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.e("test", position.toString())
                    Log.e("test", selectedItem.toString())
                }
            }
        }
    }

    private fun initControl() {
        handler.post(object : Runnable {
            override fun run() {
                if (viewModel.isStart.value!!) {
                    viewModel.runTime.value = viewModel.runTime.value!! + 1
                    viewModel.runHeart.value = MyApplication.heartRate
                    if (viewModel.runTime.value!! % 60 == 0) {
                        PostSportsRealTimeTask(object : TaskListener {
                            override fun onStart() {}

                            override fun onSuccess() {
                                toast(this@DailySportsIndoorActivity, "上传成功!")
                            }

                            override fun onFail(code: WCode) {
                                toast(this@DailySportsIndoorActivity, "上传失败！${code.error}")
                            }

                        }).execute()
                    }
                }
                handler.postDelayed(this, 1000)
            }
        })

        viewModel.isStart.observe(this, Observer {
            if (it) {
                binding.button.apply {
                    background = getDrawable(R.drawable.ic_button_finish)
                    text = "结束"
                    binding.spinner.isEnabled = false
                }
            } else {
                binding.button.apply {
                    background = getDrawable(R.drawable.ic_button_start)
                    text = "开始"
                    binding.spinner.isEnabled = true
                }
            }
        })

        binding.button.setOnClickListener {
            if (!viewModel.isStart.value!!) {
                when {
                    !MyApplication.isConnect -> toast(this, "请先连接手环！")
                    else -> {
                        reset()
                        progressBar.visibility = View.VISIBLE
                        MyApplication.mode = RunMode.Indoor
                        SportsSummaryTask(object : SimpleTaskListener<Unit> {
                            override fun onSuccess(p: Unit) {
                                progressBar.visibility = View.INVISIBLE
                                viewModel.isStart.value = true
                                DeviceUtil.startRealtime(viewModel.address)
                            }
                        }).execute()
                    }
                }
            } else {
                AlertDialog.Builder(this)
                    .setTitle("确定结束运动？")
                    .setPositiveButton("确定") { _, _ ->
                        with(viewModel) {
                            isStart.value = false
                            if (MyApplication.isConnect) {
                                DeviceUtil.stopRealTime(address)
                            }
                            progressBar.visibility = View.INVISIBLE
                            PostSportsRealTimeTask(object : TaskListener {
                                override fun onStart() {}

                                override fun onSuccess() {
                                    PostSportsFinalTask(object : TaskListener {
                                        override fun onStart() {
                                        }

                                        override fun onSuccess() {
                                            MyApplication.mode = RunMode.Stop
                                            toast(this@DailySportsIndoorActivity, "上传成功!")
                                        }

                                        override fun onFail(code: WCode) {
                                            MyApplication.mode = RunMode.Stop
                                            toast(
                                                this@DailySportsIndoorActivity,
                                                "上传失败！${code.error}"
                                            )
                                        }

                                    }).execute(
                                        runTime.value!!.toString()
                                    )
                                }

                                override fun onFail(code: WCode) {
                                    toast(this@DailySportsIndoorActivity, "上传失败！${code.error}")
                                }

                            }).execute()
                        }
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .create().show()
            }
        }
    }

    private fun reset() {
        viewModel.apply {
            runTime.value = 0
            runHeart.value = 0
        }
    }


    override fun finish() {
        if (viewModel.isStart.value!!) {
            AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("请先结束运动，否则可能造成数据丢失！")
                .setPositiveButton("确定") { _, _ -> }
                .create().show()
        } else {
            super.finish()
        }
    }

}