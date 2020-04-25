package com.zjut.wristband2.repo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DailyHeartDao {
    @Insert
    fun insert(vararg p: DailyHeart)

    @Update
    fun update(vararg p: DailyHeart)

    @Query("select * from daily_heart where utc>=:begin and utc<=:end and device_id=:device order by utc asc")
    fun findByUtcAndDevice(begin: Long, end: Long, device: String): List<DailyHeart>
}