package com.zjut.wristband2.util

import com.baidu.mapapi.model.LatLng
import com.zjut.wristband2.repo.Position
import java.util.*
import kotlin.math.*

/**
 * @author qpf
 * @date 2020-8
 * @description some tools
 */

/**
 * converts the utc to date or vice versa
 */
object TimeTransfer {
    fun utc2Date(utc: Long) = Date(utc * 1000)
    fun utcMillion2Date(utc: Long) = Date(utc)

    fun date2Utc(date: Date) = date2UtcMillion(date) / 1000
    private fun date2UtcMillion(date: Date) = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.time.time


    fun nowUtcMillion() = Calendar.getInstance().apply {
        time = Date()
    }.time.time

    fun getTodayTimeSpan() = with(date2UtcMillion(Date())) {
        TimeSpan(this, this + 86400000)
    }

    fun getToMonthTimeSpan() = with(Calendar.getInstance()) {
        time = Date()
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.DAY_OF_MONTH, 1)
        TimeSpan(time.time, date2UtcMillion(Date()) + 86400000)
    }

    fun getToWeekTimeSpan() = with(Calendar.getInstance()) {
        time = Date()
        val dayOfWeek =
            if (get(Calendar.DAY_OF_WEEK) - 1 == 0)
                7
            else
                get(Calendar.DAY_OF_WEEK) - 1
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        add(Calendar.DATE, 1 - dayOfWeek)
        TimeSpan(time.time, date2UtcMillion(Date()) + 86400000)
    }

    data class TimeSpan(
        val startTime: Long,
        val endTime: Long
    )
}


enum class RunMode(val num: Int, val mode: String) {
    Stop(0, "stop"),
    Indoor(1, "indoor"),
    Outdoor(2, "outdoor"),
    Aerobics(3, "aerobics")
}

fun getDistance(source: LatLng, target: LatLng): Double {
    val lat1 = rad(source.latitude)
    val lat2 = rad(target.latitude)
    val a = lat1 - lat2
    val b = rad(source.longitude) - rad(target.longitude)
    var s = 2 * asin(
        sqrt(
            sin(a / 2).pow(2.0) + cos(lat1) * cos(lat2) * sin(b / 2).pow(2.0)
        )
    )

    s *= 6378137.0 //earth radius
    s = (s * 10000.0).roundToInt() / 10000.0
    return s
}

private fun rad(d: Double): Double {
    return d * Math.PI / 180.00 //角度转换成弧度
}

/**
 * baidu map the gaode map
 */
fun baiduToGaode(lng: Double, lat: Double): Position {
    val x_pi = 3.14159265358979324 * 3000.0 / 180.0
    val x = lng - 0.0065
    val y = lat - 0.006
    val z = sqrt(x * x + y * y) - 0.00002 * sin(y * x_pi)
    val theta = atan2(y, x) - 0.000003 * cos(x * x_pi)
    val lngs = z * cos(theta)
    val lats = z * sin(theta)
    return Position(
        name = "",
        longitude = lngs,
        latitude = lats,
        updateTime = ""
    )
}