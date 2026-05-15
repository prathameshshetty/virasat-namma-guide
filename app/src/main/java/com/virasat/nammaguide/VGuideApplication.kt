package com.virasat.nammaguide

import android.app.Application
import androidx.work.*
import com.virasat.nammaguide.data.api.GeminiApiService
import com.virasat.nammaguide.data.db.AppDatabase
import com.virasat.nammaguide.data.repository.PassportRepository
import com.virasat.nammaguide.data.repository.SiteRepository
import com.virasat.nammaguide.worker.SyncWorker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class VGuideApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val geminiService: GeminiApiService by lazy { retrofit.create(GeminiApiService::class.java) }

    val siteRepository: SiteRepository by lazy {
        SiteRepository(database.heritageSiteDao(), database.aiQueryCacheDao(), geminiService, BuildConfig.GEMINI_API_KEY)
    }

    val passportRepository: PassportRepository by lazy {
        PassportRepository(database.checkInDao(), database.heritageSiteDao())
    }

    override fun onCreate() {
        super.onCreate()
        scheduleSyncWorker()
    }

    private fun scheduleSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<SyncWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_worker", ExistingPeriodicWorkPolicy.KEEP, request
        )
    }
}
