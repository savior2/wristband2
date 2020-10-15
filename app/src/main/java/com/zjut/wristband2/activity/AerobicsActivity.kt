package com.zjut.wristband2.activity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.zjut.wristband2.databinding.ActivityAerobicsBinding
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.repo.AerobicsPosition
import com.zjut.wristband2.task.AerobicsSummaryTask
import com.zjut.wristband2.task.PostAerobicsTask
import com.zjut.wristband2.task.TaskListener
import com.zjut.wristband2.util.*
import com.zjut.wristband2.vm.AerobicsActivityVM
import kotlin.math.abs

/**
 * @author qpf
 * @date 2020-8
 * @description aerobics exercise exhibit and control
 */
class AerobicsActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mBaiduMap: BaiduMap
    private lateinit var mLocClient: LocationClient
    private val mLocListener = MyLocationListener()

    private lateinit var viewModel: AerobicsActivityVM
    private lateinit var binding: ActivityAerobicsBinding

    private lateinit var mSensorManager: SensorManager
    private lateinit var mLocationManager: LocationManager
    private lateinit var mVibrator: Vibrator

    private lateinit var mSoundSpeedUp: MediaPlayer
    private lateinit var mSoundSlowDown: MediaPlayer
    private lateinit var mSoundNormal: MediaPlayer

    private val startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_start)
    private val finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_end)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_aerobics)
        //获取AerobicsActivity的实例
        viewModel = ViewModelProvider(this)[AerobicsActivityVM::class.java]
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
        mVibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        mSoundSpeedUp = MediaPlayer.create(this, R.raw.quick)
        mSoundSlowDown = MediaPlayer.create(this, R.raw.slow)
        mSoundNormal = MediaPlayer.create(this, R.raw.normal)
        initMap()
        initControl()
        initViewModel()

        AlertDialog.Builder(this)
            .setTitle("有氧耐力跑测试规则")
            .setMessage(
                "有氧运动测试分为两个阶段：\n\t\t第一阶段过程中，起始速度为2km/h，以2km/h、4km/h、6km/h、8km/h、10km/h、12km/h的级别进行速度递增，每速度级别保持2分钟。\n\t\t第二阶段过程中，再以12km/h，10km/h、8km/h、6km/h、4km/h、2km/h的级别进行速度递减，同样每速度级别保持两分钟。总过程持续22分钟。\n\t\t在有氧运动测试过程中，APP会对受测者进行速度语音提示以达到速度控制的效果。"
            )
            .setPositiveButton("确定") { _, _ -> }
            .create().show()
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
            } else {
                binding.button.apply {
                    background = getDrawable(R.drawable.ic_button_start)
                    text = "开始"
                }
            }
        })
        binding.button.setOnClickListener {
            if (!viewModel.isStart.value!!) {
                when {
                    !MyApplication.isConnect -> toast(this, "请先连接手环！")
                    !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> toast(
                        this,
                        "请先开启GPS！"
                    )
                    else -> {
                        viewModel.isStart.value = true
                        viewModel.positions.clear()
                        reset()
                        setProgress(true, "GPS信号搜索中，请留在原地...")
                        mBaiduMap.clear()
                        AerobicsSummaryTask().execute()
                    }
                }
            } else {
                AlertDialog.Builder(this)
                    .setTitle("确定结束测试？")
                    .setPositiveButton("确定") { _, _ ->
                        with(viewModel) {
                            isStart.value = false
                            if (MyApplication.isConnect) {
                                DeviceUtil.stopRealTime(address)
                            }
                            MyApplication.mode = RunMode.Stop
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
                            }
                            PostAerobicsTask(object : TaskListener {
                                override fun onStart() {
                                    setProgress(true, "正在上传...")
                                }

                                override fun onSuccess() {
                                    positions.clear()
                                    setProgress(false)
                                    toast(this@AerobicsActivity, "上传成功!")
                                }

                                override fun onFail(code: WCode) {
                                    positions.clear()
                                    setProgress(false)
                                    toast(this@AerobicsActivity, "上传失败！${code.error}")
                                }

                            }).execute(*positions.toTypedArray())
                        }

                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .create().show()
            }
        }
    }

    private fun initViewModel() {
        with(viewModel) {
            currentDirection.observe(this@AerobicsActivity, Observer {
                setMyLocation()
            })
            currentLocation.observe(this@AerobicsActivity, Observer {
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

    private fun speedUp() {
        mVibrator.vibrate(longArrayOf(0L, 100L, 200L, 100L), -1)
        mSoundSpeedUp.start()
    }

    private fun slowDown() {
        mVibrator.vibrate(1000)
        mSoundSlowDown.start()
    }

    private fun speedNormal() = mSoundNormal.start()

    override fun finish() {
        if (viewModel.isStart.value!!) {
            AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("请先结束测试，否则可能造成数据丢失！")
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
        mVibrator.cancel()
        mSoundSpeedUp.stop()
        mSoundSpeedUp.release()
        mSoundSlowDown.stop()
        mSoundSlowDown.release()
        mSoundNormal.stop()
        mSoundNormal.release()
        if (MyApplication.isConnect) {
            DeviceUtil.stopRealTime(viewModel.address)
        }
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
                        MyApplication.mode = RunMode.Aerobics
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
                            AerobicsPosition(
                                MyApplication.num,
                                ll.longitude.toString(),
                                ll.latitude.toString(),
                                String.format("%.2f", p0.speed).toFloat(),
                                TimeTransfer.nowUtcMillion()
                            )
                        )
                        if (runTime.value!! % 10 == 0) {
                            when (runTime.value) {
                                in 1..119 -> {
                                    when {
                                        p0.speed < 1.5 -> speedUp()
                                        p0.speed > 2.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                                in 120..239 -> {
                                    when {
                                        p0.speed < 3.5 -> speedUp()
                                        p0.speed > 4.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                                in 240..359 -> {
                                    when {
                                        p0.speed < 5.5 -> speedUp()
                                        p0.speed > 6.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                                in 360..479 -> {
                                    when {
                                        p0.speed < 7.5 -> speedUp()
                                        p0.speed > 8.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                                in 480..599 -> {
                                    when {
                                        p0.speed < 9.5 -> speedUp()
                                        p0.speed > 10.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                                in 600..719 -> {
                                    when {
                                        p0.speed < 11.5 -> speedUp()
                                        p0.speed > 12.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                                in 720..839 -> {
                                    when {
                                        p0.speed < 9.5 -> speedUp()
                                        p0.speed > 10.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                                in 840..959 -> {
                                    when {
                                        p0.speed < 7.5 -> speedUp()
                                        p0.speed > 8.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                                in 960..1079 -> {
                                    when {
                                        p0.speed < 5.5 -> speedUp()
                                        p0.speed > 6.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                                in 1080..1199 -> {
                                    when {
                                        p0.speed < 3.5 -> speedUp()
                                        p0.speed > 4.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                                in 1200..1319 -> {
                                    when {
                                        p0.speed < 1.5 -> speedUp()
                                        p0.speed > 2.5 -> slowDown()
                                        else -> speedNormal()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * maximum distance that the initial points drift
         */
        private const val MAX_DISTANCE = 20

        /**
         * minimum distance between two points on the map
         */
        private const val MIN_DISTANCE = 5

        /**
         * minimum number of initial points
         */
        private const val MIN_NUMBER = 3

        /**
         * minimum precision for each point
         */
        private const val MIN_ACCURACY = 80
    }
}
