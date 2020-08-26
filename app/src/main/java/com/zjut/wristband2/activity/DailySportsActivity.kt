package com.zjut.wristband2.activity

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.zjut.wristband2.MyApplication
import com.zjut.wristband2.R
import com.zjut.wristband2.databinding.ActivityDailySportsBinding
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.fragment.NavDeviceFragment
import com.zjut.wristband2.repo.SportsPosition
import com.zjut.wristband2.task.PostSportsFinalTask
import com.zjut.wristband2.task.PostSportsRealTimeTask
import com.zjut.wristband2.task.SportsSummaryTask
import com.zjut.wristband2.task.TaskListener
import com.zjut.wristband2.util.*
import com.zjut.wristband2.vm.DailySportsActivityVM
import kotlin.math.abs

/**
 * @author qpf
 * @date 2020-8
 * @description same as AerobicsActivity
 */
class DailySportsActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mBaiduMap: BaiduMap
    private lateinit var mLocClient: LocationClient
    private val mLocListener = MyLocationListener()

    private lateinit var viewModel: DailySportsActivityVM
    private lateinit var binding: ActivityDailySportsBinding

    private lateinit var mSensorManager: SensorManager
    private lateinit var mLocationManager: LocationManager

    private val startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_start)
    private val finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_end)

    private lateinit var notificationUtil: NotificationUtil
    private lateinit var notification: Notification


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_sports)
        viewModel = ViewModelProvider(this)[DailySportsActivityVM::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = this
        with(binding.toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Build.VERSION.SDK_INT >= 26) {
            notificationUtil = NotificationUtil(this)
            val build = notificationUtil.getAndroidChannelNotification("Wristband", "正在后台定位")
            notification = build.build()
        } else {
            val builder = Notification.Builder(this)
            val intent = Intent(this, DailySportsActivity::class.java)
            builder.setContentIntent(
                PendingIntent.getActivity(this, 0, intent, 0)
            ).setContentTitle("Wristband")
                .setSmallIcon(R.mipmap.ic_run)
                .setContentText("正在后台定位")
                .setWhen(System.currentTimeMillis())
            notification = builder.build()
        }
        notification.defaults = Notification.DEFAULT_SOUND
        initMap()
        initControl()
        initViewModel()
        getLocationPermission()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                2
            )
        } else {

        }
    }

    private fun initMap() {
        mLocClient = LocationClient(this)
        mLocClient.registerLocationListener(mLocListener)
        binding.mapView.showZoomControls(false)
        mBaiduMap = binding.mapView.map.apply {
            isMyLocationEnabled = true
            setMyLocationConfiguration(
                MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.FOLLOWING, true, null
                )
            )
            setOnMapStatusChangeListener(object : BaiduMap.OnMapStatusChangeListener {
                override fun onMapStatusChangeStart(p0: MapStatus?) {}

                override fun onMapStatusChangeStart(p0: MapStatus?, p1: Int) {}

                override fun onMapStatusChange(p0: MapStatus?) {}

                override fun onMapStatusChangeFinish(p0: MapStatus) {
                    if (!viewModel.isFirstZoom) {
                        viewModel.currentZoom.value = p0.zoom
                        viewModel.currentTarget.value = p0.target
                    }
                }
            })
        }
        with(LocationClientOption()) {
            isOpenGps = true
            setCoorType("bd09ll")
            setScanSpan(1000)
            mLocClient.locOption = this
        }
        if (!mLocClient.isStarted) {
            mLocClient.start()
        }
    }


    private fun initControl() {
        viewModel.isStart.observe(this, Observer {
            if (it) {
                binding.button.apply {
                    background = getDrawable(R.drawable.ic_button_finish)
                    text = "结束"
                }
                mLocClient.enableLocInForeground(5, notification)
            } else {
                binding.button.apply {
                    background = getDrawable(R.drawable.ic_button_start)
                    text = "开始"
                }
                mLocClient.disableLocInForeground(true)
            }
        })
        binding.button.setOnClickListener {
            if (!viewModel.isStart.value!!) {
                when {
                    !MyApplication.isConnect -> toast(this, "请先连接手环！")
                    !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> toast(
                        this,
                        "请先开启gps！"
                    )
                    else -> {
                        viewModel.isStart.value = true
                        viewModel.positions.clear()
                        reset()
                        setProgress(true, "GPS信号搜索中，请留在原地...")
                        mBaiduMap.clear()
                        MyApplication.mode = RunMode.Outdoor
                        SportsSummaryTask(null).execute()
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
                            setProgress(false)
                            if (isFirstLocate) {
                                points.clear()
                            } else {
                                isFirstLocate = true
                                runHeart.value = 0
                                runSpeed.value = 0f
                                mBaiduMap.addOverlay(
                                    MarkerOptions().position(points[points.size - 1]).icon(
                                        finishBD
                                    )
                                )
                                points.clear()
                                PostSportsRealTimeTask(object : TaskListener {
                                    override fun onStart() {}

                                    override fun onSuccess() {
                                        positions.clear()
                                        PostSportsFinalTask(object : TaskListener {
                                            override fun onStart() {
                                                setProgress(true, "正在上传...")
                                            }

                                            override fun onSuccess() {
                                                MyApplication.mode = RunMode.Stop
                                                setProgress(false)
                                                toast(this@DailySportsActivity, "上传成功!")
                                            }

                                            override fun onFail(code: WCode) {
                                                MyApplication.mode = RunMode.Stop
                                                setProgress(false)
                                                toast(
                                                    this@DailySportsActivity,
                                                    "上传失败！${code.error}"
                                                )
                                            }

                                        }).execute(
                                            runTime.value!!.toString(),
                                            runDistance.value!!.toString()
                                        )
                                    }

                                    override fun onFail(code: WCode) {
                                        toast(this@DailySportsActivity, "上传失败！${code.error}")
                                    }

                                }).execute(*positions.toTypedArray())
                            }
                        }

                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .create().show()
            }
        }
    }

    private fun initViewModel() {
        with(viewModel) {
            currentDirection.observe(this@DailySportsActivity, Observer {
                setMyLocation()
            })
            currentLocation.observe(this@DailySportsActivity, Observer {
                setMyLocation()
                setTargetLocation()
            })
        }
    }

    private fun setMyLocation() {
        val mLocData = MyLocationData.Builder().accuracy(0f)
            .direction(viewModel.currentDirection.value!!)
            .latitude(viewModel.currentLocation.value!!.latitude)
            .longitude(viewModel.currentLocation.value!!.longitude).build()
        mBaiduMap.setMyLocationData(mLocData)
    }

    private fun setTargetLocation() {
        val mMapStatus = MapStatus.Builder()
            .target(viewModel.currentTarget.value)
            .zoom(viewModel.currentZoom.value!!).build()
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus))
    }

    private fun reset() {
        viewModel.apply {
            runTime.value = 0
            runDistance.value = 0f
            runSpeed.value = 0f
            runHeart.value = 0
        }
    }

    private fun setProgress(visible: Boolean, text: String = "") {
        if (visible) {
            binding.progress.visibility = View.VISIBLE
            binding.textView.text = text
        } else {
            binding.progress.visibility = View.GONE
        }
    }

    private fun getMostAccuracyLocation(location: LatLng): LatLng? {
        with(viewModel) {
            return if (getDistance(location, lastPoint) > MAX_DISTANCE) {
                points.clear()
                lastPoint = location
                null
            } else {
                points.add(location)
                lastPoint = location
                if (points.size > MIN_NUMBER) {
                    points.clear()
                    location
                } else {
                    null
                }
            }
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

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        mSensorManager.registerListener(
            this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocClient.isStarted) {
            mLocClient.stop()
        }
        binding.mapView.onDestroy()
        mLocClient.unRegisterLocationListener(mLocListener)
        startBD?.recycle()
        finishBD?.recycle()
        if (MyApplication.isConnect) {
            DeviceUtil.stopRealTime(viewModel.address)
        }
        mLocClient.disableLocInForeground(true)
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(p0: SensorEvent) {
        if (!viewModel.isFirstZoom) {
            val x = p0.values[SensorManager.DATA_X]
            if (abs(x - viewModel.currentDirection.value!!) > 1) {
                viewModel.currentDirection.value = x
            }
        }
    }

    private inner class MyLocationListener : BDLocationListener {
        override fun onReceiveLocation(p0: BDLocation) {
            val ll = LatLng(p0.latitude, p0.longitude)
            viewModel.apply {
                currentLocation.value = ll
                if (isFirstZoom) isFirstZoom = false
            }
            with(viewModel) {
                if (isStart.value!!) {  //&& p0.locType == BDLocation.TypeGpsLocation
                    if (p0.radius > MIN_ACCURACY) return
                    if (isFirstLocate) {
                        val location =
                            getMostAccuracyLocation(ll) ?: return
                        points.add(location)
                        mBaiduMap.addOverlay(MarkerOptions().position(ll).icon(startBD))
                        isFirstLocate = false
                        setProgress(false)
                        binding.runTimeText.visibility = View.VISIBLE
                        binding.statisticLayout.visibility = View.VISIBLE
                        if (MyApplication.isConnect) {
                            DeviceUtil.startRealtime(viewModel.address)
                        }
                    } else {
                        val distance = getDistance(ll, lastPoint).toFloat()
                        runTime.value = runTime.value!! + 1
                        runSpeed.value = p0.speed
                        runHeart.value = MyApplication.heartRate
                        if (distance > MIN_DISTANCE) {
                            runDistance.value = runDistance.value!! + distance
                            points.add(ll)
                            lastPoint = ll
                            mBaiduMap.apply {
                                clear()
                                addOverlay(MarkerOptions().position(points[0]).icon(startBD))
                                addOverlay(
                                    PolylineOptions().width(10).color(-0x55010000).points(
                                        points
                                    )
                                )
                            }
                        }
                        positions.add(
                            SportsPosition(
                                MyApplication.num,
                                ll.longitude.toString(),
                                ll.latitude.toString(),
                                String.format("%.2f", p0.speed).toFloat(),
                                TimeTransfer.nowUtcMillion()
                            )
                        )
                        if (runTime.value!! % 60 == 0) {
                            PostSportsRealTimeTask(object : TaskListener {
                                override fun onStart() {}

                                override fun onSuccess() {
                                    toast(this@DailySportsActivity, "上传成功!")
                                }

                                override fun onFail(code: WCode) {
                                    toast(this@DailySportsActivity, "上传失败！${code.error}")
                                }

                            }).execute(*positions.toTypedArray())
                            positions.clear()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val MAX_DISTANCE = 80
        private const val MIN_DISTANCE = 5
        private const val MIN_NUMBER = 3
        private const val MIN_ACCURACY = 50
    }
}

