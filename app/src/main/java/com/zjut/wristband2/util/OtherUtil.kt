package com.zjut.wristband2.util

import com.baidu.mapapi.model.LatLng
import java.util.*
import kotlin.math.*

object TimeTransfer {
    fun utc2Date(utc: Long) = Date(utc * 1000)
    fun utcMillion2Date(utc: Long) = Date()

    fun date2Utc(date: Date) = date2UtcMillion(date) / 1000
    fun date2UtcMillion(date: Date) = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.time.time


    fun nowUtcMillion() = Calendar.getInstance().apply {
        time = Date()
    }.time.time
}

enum class RunMode {
    Stop,
    Normal,
    Aerobics
}

fun getDistance(source: LatLng, target: LatLng): Double {
    val lat1 = rad(source.latitude)
    val lat2 = rad(target.latitude)
    val a = lat1 - lat2 //纬度之差
    val b = rad(source.longitude) - rad(target.longitude) //经度之差
    var s = 2 * asin(
        sqrt(
            sin(a / 2).pow(2.0) + cos(lat1) * cos(lat2) * sin(b / 2).pow(2.0)
        )
    ) //计算两点距离的公式

    s *= 6378137.0//弧长乘地球半径（半径为米）
    s = (s * 10000.0).roundToInt() / 10000.0//精确距离的数值
    return s
}

private fun rad(d: Double): Double {
    return d * Math.PI / 180.00 //角度转换成弧度
}