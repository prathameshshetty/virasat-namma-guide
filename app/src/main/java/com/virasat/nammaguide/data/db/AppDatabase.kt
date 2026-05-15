package com.virasat.nammaguide.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.virasat.nammaguide.data.db.dao.AIQueryCacheDao
import com.virasat.nammaguide.data.db.dao.CheckInDao
import com.virasat.nammaguide.data.db.dao.HeritageSiteDao
import com.virasat.nammaguide.data.db.entity.AIQueryCache
import com.virasat.nammaguide.data.db.entity.CheckIn
import com.virasat.nammaguide.data.db.entity.HeritageSite

@Database(
    entities = [HeritageSite::class, CheckIn::class, AIQueryCache::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun heritageSiteDao(): HeritageSiteDao
    abstract fun checkInDao(): CheckInDao
    abstract fun aiQueryCacheDao(): AIQueryCacheDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "namma_guide_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
