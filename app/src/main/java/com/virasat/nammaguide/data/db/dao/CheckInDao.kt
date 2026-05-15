package com.virasat.nammaguide.data.db.dao

import androidx.room.*
import com.virasat.nammaguide.data.db.entity.CheckIn
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC")
    fun getAllCheckIns(): Flow<List<CheckIn>>

    @Query("SELECT * FROM check_ins WHERE siteId = :siteId LIMIT 1")
    suspend fun getCheckInForSite(siteId: String): CheckIn?

    @Query("SELECT EXISTS(SELECT 1 FROM check_ins WHERE siteId = :siteId)")
    suspend fun hasCheckedIn(siteId: String): Boolean

    @Insert
    suspend fun insert(checkIn: CheckIn): Long

    @Query("UPDATE check_ins SET checkOutTime = :time WHERE siteId = :siteId AND checkOutTime IS NULL")
    suspend fun checkOut(siteId: String, time: Long)

    @Query("SELECT COUNT(*) FROM check_ins")
    fun getTotalCheckIns(): Flow<Int>
}
