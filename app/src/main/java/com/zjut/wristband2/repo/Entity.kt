package com.zjut.wristband2.repo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "daily_heart")
class DailyHeart {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    @ColumnInfo(name = "heart_rate")
    var heartRate = 0
    @ColumnInfo(name = "utc")
    var utc = 0L
    @ColumnInfo(name = "device_id")
    var deviceId = ""
    @ColumnInfo(name = "status")
    var status = 0

    constructor(heartRate: Int, utc: Long, deviceId: String) {
        this.heartRate = heartRate
        this.utc = utc
        this.deviceId = deviceId
    }
}