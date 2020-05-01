package com.zjut.wristband2.repo

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zjut.wristband2.MyApplication

@Database(
    entities = [DailyHeart::class,
        AerobicsSummary::class,
        AerobicsHeart::class,
        AerobicsPosition::class,
        SportsSummary::class,
        SportsPosition::class,
        SportsHeart::class],
    version = 5
)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getDailyHeartDao(): DailyHeartDao
    abstract fun getAerobicsSummaryDao(): AerobicsSummaryDao
    abstract fun getAerobicsHeartDao(): AerobicsHeartDao
    abstract fun getAerobicsPositionDao(): AerobicsPositionDao
    abstract fun getSportsSummaryDao(): SportsSummaryDao
    abstract fun getSportsPositionDao(): SportsPositionDao
    abstract fun getSportsHeartDao(): SportsHeartDao

    companion object {
        val instance by lazy {
            Room.databaseBuilder(
                MyApplication.context,
                MyDatabase::class.java,
                "wristband_database"
            ).build()
        }
    }
}