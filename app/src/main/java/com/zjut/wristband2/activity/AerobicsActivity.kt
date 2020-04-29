package com.zjut.wristband2.activity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MyLocationConfiguration
import com.zjut.wristband2.MyApplication
import com.zjut.wristband2.R
import com.zjut.wristband2.databinding.ActivityAerobicsBinding
import com.zjut.wristband2.util.toast
import com.zjut.wristband2.vm.AerobicsActivityVM

class AerobicsActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mBaiduMap: BaiduMap
    private lateinit var mLocClient: LocationClient
    private val mLocListener = MyLocationListener()

    private lateinit var viewModel: AerobicsActivityVM
    private lateinit var binding: ActivityAerobicsBinding

    private lateinit var mSensorManager: SensorManager
    private lateinit var mLocationManager: LocationManager
    private lateinit var mVibrator: Vibrator

    private val startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_start)
    private val finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_end)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_aerobics)
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
        initMap()
        initControl()
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

                override fun onMapStatusChangeFinish(p0: MapStatus?) {}
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
                        "请先开启gps！"
                    )
                    else -> {
                        viewModel.isStart.value = true
                        reset()
                        setProgress(true, "GPS信号搜索中，请留在原地...")

                    }
                }

            } else {
                viewModel.isStart.value = false
            }
        }
    }

    private fun reset() {
        viewModel.apply {
            runTime.value = 0
            runDistance.value = 0f
            runSpeed.value = 0f
            runHeart.value = 0
        }
    }

    private fun setProgress(visible: Boolean, text: String) {
        if (visible) {
            binding.progress.visibility = View.VISIBLE
            binding.textView.text = text
        } else {
            binding.progress.visibility = View.GONE
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
        startBD?.recycle()
        finishBD?.recycle()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(p0: SensorEvent?) {}


    private inner class MyLocationListener : BDLocationListener {
        override fun onReceiveLocation(p0: BDLocation?) {
        }
    }

}
