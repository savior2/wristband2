package com.zjut.wristband2.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng

import com.zjut.wristband2.R
import com.zjut.wristband2.activity.AerobicsActivity
import kotlinx.android.synthetic.main.fragment_aerobics.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class AerobicsFragment : Fragment() {

    private lateinit var mBaiduMap: BaiduMap

    private lateinit var mLocClient: LocationClient
    private val mLocListener = MyLocListener()

    private var isFirstZoom = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_aerobics, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mLocClient = LocationClient(requireContext())
        mBaiduMap = mapView2.map.apply {
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
        enter.setOnClickListener {
            startActivity(Intent(requireContext(), AerobicsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        mapView2.onResume()
        if (!mLocClient.isStarted) {
            mLocClient.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mapView2.onPause()
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
