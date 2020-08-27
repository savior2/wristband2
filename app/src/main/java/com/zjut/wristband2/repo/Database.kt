package com.zjut.wristband2.repo

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zjut.wristband2.MyApplication

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
@Database(
    entities = [DailyHeart::class,
        AerobicsSummary::class,
        AerobicsHeart::class,
        AerobicsPosition::class,
        SportsSummary::class,
        SportsPosition::class,
        SportsHeart::class],
    version = 6
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
            )
                .addMigrations(MIGRATION_5_6)
                .build()
        }
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE sports_summary ADD COLUMN mode TEXT")
            }
        }
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
    }
}