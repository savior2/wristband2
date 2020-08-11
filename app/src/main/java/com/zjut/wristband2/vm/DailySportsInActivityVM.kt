package com.zjut.wristband2.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.zjut.wristband2.util.SpUtil

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class DailySportsInActivityVM(app: Application) : AndroidViewModel(app) {

    val address by lazy {
        SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(SpUtil.SpAccount.MAC_ADDRESS, "")!!
    }

    val runTime by lazy {
        MutableLiveData<Int>().apply { value = 0 }
    }


    val runHeart by lazy {
        MutableLiveData<Int>().apply { value = 0 }
    }

    val isStart by lazy {
        MutableLiveData<Boolean>().apply { value = false }
    }
}