package com.zjut.wristband2.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.baidu.mapapi.model.LatLng
import com.zjut.wristband2.repo.AerobicsHeart
import com.zjut.wristband2.repo.AerobicsPosition
import com.zjut.wristband2.util.SpUtil

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class AerobicsActivityVM(app: Application) : AndroidViewModel(app) {
    val address by lazy {
        SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(SpUtil.SpAccount.MAC_ADDRESS, "")!!
    }

    val isStart by lazy {
        MutableLiveData<Boolean>().apply { value = false }
    }
    val runTime by lazy {
        MutableLiveData<Int>().apply { value = 0 }
    }
    val runDistance by lazy {
        MutableLiveData<Float>().apply { value = 0F }
    }
    val runSpeed by lazy {
        MutableLiveData<Float>().apply { value = 0F }
    }
    val runHeart by lazy {
        MutableLiveData<Int>().apply { value = 0 }
    }

    val currentLocation by lazy {
        MutableLiveData<LatLng>().apply { value = LatLng(0.0, 0.0) }
    }
    val currentDirection by lazy {
        MutableLiveData<Float>().apply { value = 0f }
    }
    val currentZoom by lazy {
        MutableLiveData<Float>().apply { value = 18f }
    }
    val currentTarget by lazy {
        MutableLiveData<LatLng>().apply { value = LatLng(0.0, 0.0) }
    }

    var isFirstZoom = true

    var isFirstLocate = true

    var lastPoint = LatLng(0.0, 0.0)

    val points = arrayListOf<LatLng>()

    val positions = arrayListOf<AerobicsPosition>()
}