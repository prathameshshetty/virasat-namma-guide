package com.virasat.nammaguide.data.db.dao

import androidx.room.*
import com.virasat.nammaguide.data.db.entity.AIQueryCache
import kotlinx.coroutines.flow.Flow

@Dao
interface AIQueryCacheDao {
    @Query("SELECT * FROM ai_query_cache WHERE siteId = :siteId ORDER BY timestamp DESC")
    fun getCacheForSite(siteId: String): Flow<List<AIQueryCache>>

    @Query("""SELECT * FROM ai_query_cache WHERE
        siteId = :siteId AND question = :question AND language = :language LIMIT 1""")
    suspend fun getCachedAnswer(siteId: String, question: String, language: String): AIQueryCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: AIQueryCache)

    @Query("DELETE FROM ai_query_cache WHERE timestamp < :cutoff")
    suspend fun deleteOld(cutoff: Long)
}
