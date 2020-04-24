package com.zjut.wristband2.util

import java.util.*

object TimeTransfer {
    fun utc2Date(utc: Long) = Date(utc * 1000)
    fun utcMillion2Date(utc: Long) = Date()

    private val instance by lazy {
        Calendar.getInstance()
    }
}