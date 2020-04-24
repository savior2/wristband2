package com.zjut.wristband2.repo

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zjut.wristband2.MyApplication

@Database(entities = [DailyHeart::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getDailyHeartDao(): DailyHeartDao

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