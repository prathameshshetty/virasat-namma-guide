package com.virasat.nammaguide.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import com.virasat.nammaguide.MainActivity
import com.virasat.nammaguide.R
import java.util.Locale

class AudioPlaybackService : Service() {

    private val binder = AudioBinder()
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var pendingText: String? = null
    private var siteName: String = ""
    private var currentSpeed: Float = 1.0f

    inner class AudioBinder : Binder() {
        fun getService(): AudioPlaybackService = this@AudioPlaybackService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                tts?.setSpeechRate(currentSpeed)
                ttsReady = true
                pendingText?.let { speak(it) }
                pendingText = null
            }
        }
    }

    fun playAudio(text: String, name: String) {
        siteName = name
        startForeground(NOTIF_ID, buildNotification("Playing: $name"))
        if (ttsReady) speak(text) else pendingText = text
    }

    fun pause() {
        tts?.stop()
        startForeground(NOTIF_ID, buildNotification("Paused: $siteName"))
    }

    fun resume() {
        pendingText?.let { speak(it) }
    }

    fun stop() {
        tts?.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun setSpeed(speed: Float) {
        currentSpeed = speed
        tts?.setSpeechRate(speed)
    }

    fun isPlaying() = tts?.isSpeaking ?: false

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "audio_guide")
    }

    private fun buildNotification(text: String): Notification {
        val pi = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_audio)
            .setContentIntent(pi)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Audio Guide", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "audio_guide_channel"
        const val NOTIF_ID = 1001
    }
}
