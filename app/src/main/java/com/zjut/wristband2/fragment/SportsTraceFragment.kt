package com.zjut.wristband2.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.zjut.wristband2.R
import com.zjut.wristband2.repo.MyDatabase
import com.zjut.wristband2.repo.SportsPosition
import com.zjut.wristband2.task.SportsPositionTask
import com.zjut.wristband2.task.SportsPositionTaskListener
import com.zjut.wristband2.vm.SummaryOnceActivityVM
import kotlinx.android.synthetic.main.activity_summary_once.*
import kotlinx.android.synthetic.main.fragment_sports_trace.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class SportsTraceFragment : Fragment() {

    private lateinit var viewModel: SummaryOnceActivityVM
    private lateinit var mBaiduMap: BaiduMap

    private val startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_start)
    private val finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_end)

    private val mPoints = arrayListOf<LatLng>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sports_trace, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SummaryOnceActivityVM::class.java]
        mBaiduMap = mapView.map
        SportsPositionTask(object : SportsPositionTaskListener {
            override fun onSuccess(p: List<SportsPosition>) {
                for (i in p) {
                    mPoints.add(LatLng(i.latitude.toDouble(), i.longitude.toDouble()))
                }
                if (mPoints.isNotEmpty()) {
                    locate()
                }
                if (mPoints.size > 2) {
                    mBaiduMap.apply {
                        addOverlay(MarkerOptions().position(mPoints[0]).icon(startBD))
                        addOverlay(
                            PolylineOptions().width(8).color(-0x55010000).points(mPoints)
                        )
                        addOverlay(
                            MarkerOptions().position(mPoints[mPoints.size - 1]).icon(finishBD)
                        )
                    }
                }
            }

        }).execute(viewModel.id)
    }

    private fun locate() {
        val mMapStatus = MapStatus.Builder().target(mPoints[mPoints.size / 2]).zoom(18F).build()
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        requireActivity().viewPager2.isUserInputEnabled = false
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        startBD?.recycle()
        finishBD?.recycle()
    }
}
