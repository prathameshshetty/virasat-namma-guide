package com.virasat.nammaguide.data.repository

import com.virasat.nammaguide.data.api.GeminiApiService
import com.virasat.nammaguide.data.db.dao.AIQueryCacheDao
import com.virasat.nammaguide.data.db.dao.HeritageSiteDao
import com.virasat.nammaguide.data.db.entity.AIQueryCache
import com.virasat.nammaguide.data.db.entity.HeritageSite
import com.virasat.nammaguide.data.model.GContent
import com.virasat.nammaguide.data.model.GPart
import com.virasat.nammaguide.data.model.GeminiRequest
import com.virasat.nammaguide.data.model.SystemInstruction
import kotlinx.coroutines.flow.Flow

class SiteRepository(
    private val siteDao: HeritageSiteDao,
    private val cacheDao: AIQueryCacheDao,
    private val gemini: GeminiApiService,
    private val apiKey: String
) {
    val allSites: Flow<List<HeritageSite>> = siteDao.getAllSites()
    val visitedCount: Flow<Int> = siteDao.getVisitedCount()

    suspend fun getSiteById(id: String) = siteDao.getSiteById(id)

    fun searchSites(query: String) = siteDao.searchSites(query)

    fun getCacheForSite(siteId: String) = cacheDao.getCacheForSite(siteId)

    suspend fun seedIfEmpty() {
        if (siteDao.getCount() == 0) siteDao.insertAll(SeedData.heritageSites)
    }

    suspend fun getOrFetchDescription(site: HeritageSite, language: String): String {
        if (site.description.isNotEmpty()) return site.description
        return try {
            val lang = if (language == "kn") "Kannada" else "English"
            val prompt = "Write a detailed historical description of ${site.nameEn} in Karnataka in $lang. Dynasty: ${site.dynasty}, Period: ${site.period}, District: ${site.district}, Type: ${site.siteType}. Include architectural significance, historical importance, and interesting facts in 3 paragraphs."
            val response = gemini.generateContent(apiKey, buildRequest(prompt))
            val desc = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            if (desc.isNotEmpty()) siteDao.updateDescription(site.id, desc)
            desc
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun askGuide(siteId: String, siteName: String, question: String, language: String): String {
        val cached = cacheDao.getCachedAnswer(siteId, question, language)
        if (cached != null) return cached.answer
        return try {
            val context = if (language == "kn") "ಸ್ಥಳ: $siteName\nಪ್ರಶ್ನೆ: $question" else "Site: $siteName\nQuestion: $question"
            val response = gemini.generateContent(apiKey, buildRequest(context))
            val answer = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Sorry, I could not get an answer right now."
            cacheDao.insert(AIQueryCache(siteId = siteId, question = question, answer = answer, language = language, timestamp = System.currentTimeMillis()))
            answer
        } catch (e: Exception) {
            "Could not connect to AI guide. Please check your internet connection."
        }
    }

    private fun buildRequest(prompt: String) = GeminiRequest(
        contents = listOf(GContent("user", listOf(GPart(prompt)))),
        systemInstruction = SystemInstruction(listOf(GPart(GUIDE_PERSONA)))
    )

    companion object {
        private const val GUIDE_PERSONA = "You are Namma Guide, a knowledgeable and enthusiastic heritage guide specializing in Karnataka's history and culture. You provide accurate, engaging, and concise information about heritage sites. You can respond in both English and Kannada based on the language of the question."
    }
}
