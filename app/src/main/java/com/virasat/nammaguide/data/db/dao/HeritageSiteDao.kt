package com.virasat.nammaguide.data.db.dao

import androidx.room.*
import com.virasat.nammaguide.data.db.entity.HeritageSite
import kotlinx.coroutines.flow.Flow

@Dao
interface HeritageSiteDao {
    @Query("SELECT * FROM heritage_sites ORDER BY nameEn")
    fun getAllSites(): Flow<List<HeritageSite>>

    @Query("SELECT * FROM heritage_sites WHERE id = :id")
    suspend fun getSiteById(id: String): HeritageSite?

    @Query("""SELECT * FROM heritage_sites WHERE
        nameEn LIKE '%' || :q || '%' OR
        nameKn LIKE '%' || :q || '%' OR
        district LIKE '%' || :q || '%'""")
    fun searchSites(q: String): Flow<List<HeritageSite>>

    @Query("SELECT * FROM heritage_sites WHERE siteType = :type ORDER BY nameEn")
    fun getSitesByType(type: String): Flow<List<HeritageSite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sites: List<HeritageSite>)

    @Update
    suspend fun update(site: HeritageSite)

    @Query("UPDATE heritage_sites SET description = :desc WHERE id = :id")
    suspend fun updateDescription(id: String, desc: String)

    @Query("UPDATE heritage_sites SET isVisited = 1 WHERE id = :id")
    suspend fun markVisited(id: String)

    @Query("SELECT COUNT(*) FROM heritage_sites")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM heritage_sites WHERE isVisited = 1")
    fun getVisitedCount(): Flow<Int>
}
