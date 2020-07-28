package com.zjut.wristband2.repo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AerobicsJson(
    val token: String,
    val studentId: String,
    val deviceId: String,
    val startTime: String,
    val position: String,
    val speed: String,
    val heartRate: String
)

data class SportsDetailJson(
    val time: String,
    val heartRate: String,
    val position: String
)

data class SportsRealtimeJson(
    val token: String,
    val studentId: String,
    val deviceId: String,
    val mode: String,
    val detail: List<SportsDetailJson>
)

data class SportsSummaryJson(
    val token: String,
    val studentId: String,
    val deviceId: String,
    val mode: String,
    val startTime: String,
    val exerciseTime: String,
    val distance: String,
    val maxHeartRate: String,
    val avgHeartRate: String,
    val steps: String = "0",
    val calorie: String = "0.0",
    val maxStepRate: String = "0.0",
    val avgStepRate: String = "0.0"
)

data class Version(
    @SerializedName("newVersion")
    val newVersion: String,
    @SerializedName("updateInfo")
    val updateInfo: String,
    @SerializedName("url")
    val url: String
) : Serializable

data class Position(
    @SerializedName("name")
    val name: String,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("update_time")
    val updateTime: String
) : Serializable