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

@Entity(tableName = "aerobics_summary")
class AerobicsSummary {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "student_id")
    var sid = ""

    @ColumnInfo(name = "device_id")
    var deviceId = ""

    @ColumnInfo(name = "start_utc")
    var startUtc = 0L

    @ColumnInfo(name = "status")
    var status = 0

    constructor(sid: String, deviceId: String, startUtc: Long) {
        this.sid = sid
        this.deviceId = deviceId
        this.startUtc = startUtc
    }
}

@Entity(tableName = "aerobics_heart")
class AerobicsHeart {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "summary_id")
    var summaryId = 0L

    @ColumnInfo(name = "utc")
    var utc = 0L

    @ColumnInfo(name = "rate")
    var rate = 0

    constructor(summaryId: Long, rate: Int, utc: Long) {
        this.summaryId = summaryId
        this.rate = rate
        this.utc = utc
    }
}

@Entity(tableName = "aerobics_position")
class AerobicsPosition {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "summary_id")
    var summaryId = 0L

    @ColumnInfo(name = "longitude")
    var longitude = ""

    @ColumnInfo(name = "latitude")
    var latitude = ""

    @ColumnInfo(name = "speed")
    var speed = 0F

    @ColumnInfo(name = "utc")
    var utc = 0L

    constructor(summaryId: Long, longitude: String, latitude: String, speed: Float, utc: Long) {
        this.summaryId = summaryId
        this.longitude = longitude
        this.latitude = latitude
        this.speed = speed
        this.utc = utc
    }
}

@Entity(tableName = "sports_summary")
class SportsSummary {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "student_id")
    var sid = ""

    @ColumnInfo(name = "device_id")
    var deviceId = ""

    @ColumnInfo(name = "start_utc")
    var startUtc = 0L

    @ColumnInfo(name = "exercise_time")
    var exerciseTime = 0L

    @ColumnInfo(name = "steps")
    var steps = 0

    @ColumnInfo(name = "calorie")
    var calorie = 0F

    @ColumnInfo(name = "distance")
    var distance = 0F

    @ColumnInfo(name = "max_heart_rate")
    var maxHeartRate = 0

    @ColumnInfo(name = "avg_heart_rate")
    var avgHeartRate = 0

    @ColumnInfo(name = "max_step_rate")
    var maxStepRate = 0

    @ColumnInfo(name = "avg_step_rate")
    var avgStepRate = 0

    @ColumnInfo(name = "mode")
    var mode: String? = null

    @ColumnInfo(name = "status")
    var status = 0
}


@Entity(tableName = "sports_heart")
class SportsHeart {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "sports_id")
    var sportsId = 0L

    @ColumnInfo(name = "rate")
    var rate = 0

    @ColumnInfo(name = "utc")
    var utc = 0L

    @ColumnInfo(name = "status")
    var status = 0

    constructor(sportsId: Long, rate: Int, utc: Long) {
        this.sportsId = sportsId
        this.rate = rate
        this.utc = utc
    }
}


@Entity(tableName = "sports_position")
class SportsPosition {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "sports_id")
    var sportsId = 0L

    @ColumnInfo(name = "longitude")
    var longitude = ""

    @ColumnInfo(name = "latitude")
    var latitude = ""

    @ColumnInfo(name = "speed")
    var speed = 0F

    @ColumnInfo(name = "utc")
    var utc = 0L

    @ColumnInfo(name = "status")
    var status = 0

    constructor(sportsId: Long, longitude: String, latitude: String, speed: Float, utc: Long) {
        this.sportsId = sportsId
        this.longitude = longitude
        this.latitude = latitude
        this.speed = speed
        this.utc = utc
    }
}
