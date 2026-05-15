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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SiteRepository(
    private val siteDao: HeritageSiteDao,
    private val cacheDao: AIQueryCacheDao,
    private val gemini: GeminiApiService,
    private val apiKey: String,
    private val chatApiKey: String
) {
    val allSites: Flow<List<HeritageSite>> = siteDao.getAllSites()
    val visitedCount: Flow<Int> = siteDao.getVisitedCount()

    private val apiMutex = Mutex()
    private var lastRequestTime = 0L

    suspend fun getSiteById(id: String) = siteDao.getSiteById(id)

    fun searchSites(query: String) = siteDao.searchSites(query)

    fun getCacheForSite(siteId: String) = cacheDao.getCacheForSite(siteId)

    @Suppress("unused")
    suspend fun getOrFetchDescription(site: HeritageSite, language: String) = site.description

    suspend fun seedIfEmpty() {
        if (siteDao.getCount() == 0) siteDao.insertAll(SeedData.heritageSites)
    }

    suspend fun askGuide(siteId: String, siteName: String, dynasty: String, period: String, question: String, language: String): String {
        val cached = cacheDao.getCachedAnswer(siteId, question, language)
        if (cached != null) return cached.answer

        val prompt = if (language == "kn")
            "ಸ್ಥಳ: $siteName (ರಾಜವಂಶ: $dynasty, ಕಾಲ: $period)\nಪ್ರಶ್ನೆ: $question"
        else
            "Site: $siteName | Dynasty: $dynasty | Period: $period\nQuestion: $question"

        return apiMutex.withLock {
            // Enforce minimum 2s gap between requests
            val elapsed = System.currentTimeMillis() - lastRequestTime
            if (elapsed < 2000L) delay(2000L - elapsed)

            try {
                val response = gemini.generateContent(chatApiKey, buildRequest(prompt))
                val answer = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Sorry, I could not get an answer."
                cacheDao.insert(AIQueryCache(siteId = siteId, question = question, answer = answer, language = language, timestamp = System.currentTimeMillis()))
                lastRequestTime = System.currentTimeMillis()
                answer
            } catch (e: HttpException) {
                if (e.code() == 429) {
                    delay(15000L)
                    try {
                        val response = gemini.generateContent(chatApiKey, buildRequest(prompt))
                        val answer = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                            ?: "Sorry, I could not get an answer."
                        cacheDao.insert(AIQueryCache(siteId = siteId, question = question, answer = answer, language = language, timestamp = System.currentTimeMillis()))
                        lastRequestTime = System.currentTimeMillis()
                        answer
                    } catch (e2: Exception) {
                        "Too many requests. Please wait 30 seconds and try again."
                    }
                } else {
                    "AI error (${e.code()}). Please try again."
                }
            } catch (e: UnknownHostException) {
                "No internet connection."
            } catch (e: SocketTimeoutException) {
                "Request timed out. Please try again."
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
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
