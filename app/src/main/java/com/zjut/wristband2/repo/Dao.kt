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

@Dao
interface AerobicsSummaryDao {
    @Insert
    fun insert(p: AerobicsSummary): Long

    @Query("select * from aerobics_summary where id = :id")
    fun findById(id: Long): AerobicsSummary
}

@Dao
interface AerobicsHeartDao {
    @Insert
    fun insert(vararg p: AerobicsHeart)

    @Query("select * from aerobics_heart where summary_id = :id order by utc asc")
    fun findBySummaryId(id: Long): List<AerobicsHeart>
}

@Dao
interface AerobicsPositionDao {
    @Insert
    fun insert(vararg p: AerobicsPosition)


    @Query("select * from aerobics_position where summary_id = :id order by utc asc")
    fun findBySummaryId(id: Long): List<AerobicsPosition>
}