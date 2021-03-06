package com.zjut.wristband2.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class DailyHeartActivityVM : ViewModel() {
    /**
     * the selected date
     */
    private val _date = MutableLiveData<Date>().also { it.value = Date() }

    val date: LiveData<Date>
        get() = _date

    fun setDate(d: Date) {
        _date.value = d
    }
}