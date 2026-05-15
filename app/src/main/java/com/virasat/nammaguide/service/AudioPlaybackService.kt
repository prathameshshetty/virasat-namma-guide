package com.virasat.nammaguide.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.virasat.nammaguide.MainActivity
import com.virasat.nammaguide.R

class AudioPlaybackService : Service() {

    private val binder = AudioBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var siteName: String = ""

    inner class AudioBinder : Binder() {
        fun getService(): AudioPlaybackService = this@AudioPlaybackService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    fun playAudio(url: String, name: String) {
        siteName = name
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { start() }
        }
        startForeground(NOTIF_ID, buildNotification("Playing: $name"))
    }

    fun pause() {
        mediaPlayer?.pause()
        startForeground(NOTIF_ID, buildNotification("Paused: $siteName"))
    }

    fun resume() {
        mediaPlayer?.start()
        startForeground(NOTIF_ID, buildNotification("Playing: $siteName"))
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun setSpeed(speed: Float) {
        mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(speed) ?: return
    }

    fun isPlaying() = mediaPlayer?.isPlaying ?: false

    private fun buildNotification(text: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
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
        super.onDestroy()
        mediaPlayer?.release()
    }

    companion object {
        const val CHANNEL_ID = "audio_guide_channel"
        const val NOTIF_ID = 1001
    }
}
