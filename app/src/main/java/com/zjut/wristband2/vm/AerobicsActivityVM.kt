package com.zjut.wristband2.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class AerobicsActivityVM(app: Application) : AndroidViewModel(app) {
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
}