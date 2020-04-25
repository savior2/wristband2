package com.zjut.wristband2.util

import java.util.*

object TimeTransfer {
    fun utc2Date(utc: Long) = Date(utc * 1000)
    fun utcMillion2Date(utc: Long) = Date()

    fun date2Utc(date: Date) = date2UtcMillion(date)/1000
    fun date2UtcMillion(date: Date) = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.time.time


    private val instance by lazy {
        Calendar.getInstance()
    }


}