package com.virasat.nammaguide.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.virasat.nammaguide.VGuideApplication

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as VGuideApplication
            app.siteRepository.seedIfEmpty()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
