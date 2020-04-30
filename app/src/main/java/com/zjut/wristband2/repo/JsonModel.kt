package com.zjut.wristband2.repo

data class AerobicsJson(
    val token: String,
    val studentId: String,
    val deviceId: String,
    val startTime: String,
    val position: String,
    val speed: String,
    val heartRate: String
)