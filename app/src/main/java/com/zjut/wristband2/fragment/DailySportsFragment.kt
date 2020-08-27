package com.zjut.wristband2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.zjut.wristband2.R
import com.zjut.wristband2.activity.DailySportsActivity
import com.zjut.wristband2.activity.DailySportsIndoorActivity
import com.zjut.wristband2.activity.DailySportsOutdoorActivity
import com.zjut.wristband2.util.isNetworkConnected
import kotlinx.android.synthetic.main.fragment_daily_sports.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class DailySportsFragment : Fragment() {

    private lateinit var mBaiduMap: BaiduMap

    private lateinit var mLocClient: LocationClient
    private val mLocListener = MyLocListener()

    private var isFirstZoom = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily_sports, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mLocClient = LocationClient(requireContext())
        mBaiduMap = mapView.map.apply {
            isMyLocationEnabled = true
            setMyLocationConfiguration(
                MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.FOLLOWING, false, null
                )
            )
        }
        with(LocationClientOption()) {
            isOpenGps = true
            setCoorType("bd09ll")
            mLocClient.locOption = this
        }
        mLocClient.registerLocationListener(mLocListener)
        outButton.setOnClickListener {
            if (isNetworkConnected()) {
                startActivity(Intent(requireContext(), DailySportsActivity::class.java))
            } else {
                startActivity(Intent(requireContext(), DailySportsOutdoorActivity::class.java))
            }
        }
        inButton.setOnClickListener {
            startActivity(Intent(requireContext(), DailySportsIndoorActivity::class.java))
        }
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
        if (!mLocClient.isStarted) {
            mLocClient.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        if (mLocClient.isStarted) {
            mLocClient.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocClient.unRegisterLocationListener(mLocListener)
    }

    private inner class MyLocListener : BDLocationListener {
        override fun onReceiveLocation(p0: BDLocation) {
            val mLocData = MyLocationData.Builder()
                .longitude(p0.longitude)
                .latitude(p0.latitude)
                .build()
            mBaiduMap.setMyLocationData(mLocData)
            if (isFirstZoom) {
                mBaiduMap.apply {
                    val mMapStatus =
                        MapStatus.Builder().target(LatLng(p0.latitude, p0.longitude)).zoom(18f)
                            .build()
                    animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus))
                }
            }
            isFirstZoom = false
        }

    }
}
