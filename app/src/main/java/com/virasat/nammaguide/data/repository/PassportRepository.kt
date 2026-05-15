package com.virasat.nammaguide.data.repository

import com.virasat.nammaguide.data.db.dao.CheckInDao
import com.virasat.nammaguide.data.db.dao.HeritageSiteDao
import com.virasat.nammaguide.data.db.entity.CheckIn
import kotlinx.coroutines.flow.Flow

class PassportRepository(
    private val checkInDao: CheckInDao,
    private val siteDao: HeritageSiteDao
) {
    val allCheckIns: Flow<List<CheckIn>> = checkInDao.getAllCheckIns()
    val totalCheckIns: Flow<Int> = checkInDao.getTotalCheckIns()

    suspend fun checkIn(siteId: String, method: String, lat: Double? = null, lon: Double? = null): Boolean {
        if (checkInDao.hasCheckedIn(siteId)) return false
        checkInDao.insert(CheckIn(siteId = siteId, timestamp = System.currentTimeMillis(), method = method, latitude = lat, longitude = lon))
        siteDao.markVisited(siteId)
        return true
    }

    suspend fun hasCheckedIn(siteId: String) = checkInDao.hasCheckedIn(siteId)
}
