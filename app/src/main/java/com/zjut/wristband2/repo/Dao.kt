package com.zjut.wristband2.repo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
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

@Dao
interface SportsSummaryDao {
    @Insert
    fun insert(p: SportsSummary): Long

    @Update
    fun update(p: SportsSummary)

    @Query("select * from sports_summary where id = :id")
    fun findById(id: Long): SportsSummary

    @Query("select * from sports_summary where start_utc>= :start and start_utc <= :end order by id")
    fun findByTime(start: Long, end: Long): List<SportsSummary>

    @Query("select * from sports_summary order by id desc")
    fun findAll(): List<SportsSummary>
}


@Dao
interface SportsPositionDao {
    @Insert
    fun insert(vararg p: SportsPosition)

    @Query("select * from sports_position where sports_id = :id  order by id asc")
    fun findBySportsId(id: Long): List<SportsPosition>
}

@Dao
interface SportsHeartDao {
    @Insert
    fun insert(vararg p: SportsHeart)

    @Update
    fun update(vararg p: SportsHeart)

    @Query("select * from sports_heart where sports_id = :id and status = 0 order by utc asc")
    fun findBySportsIdAndStatus(id: Long): List<SportsHeart>

    @Query("select * from sports_heart where sports_id = :id  order by rate desc")
    fun findBySportsId(id: Long): List<SportsHeart>

    @Query("select * from sports_heart where sports_id = :id  order by id asc")
    fun findBySportsId2(id: Long): List<SportsHeart>
}