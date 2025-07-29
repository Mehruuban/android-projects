package com.mehru.calmly

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.Service.START_STICKY
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MediaPlaybackService : Service() {

    private var mediaPlayer: android.media.MediaPlayer? = null
    private val CHANNEL_ID = "calmly_channel"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val soundResId = intent?.getIntExtra("soundResId", 0) ?: 0
        if (soundResId != 0) {
            startMedia(soundResId)
        }
        return START_STICKY
    }

    @SuppressLint("ForegroundServiceType")
    private fun startMedia(soundResId: Int) {
        createNotificationChannel()

        // Play
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, soundResId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        // Notification
        val pauseIntent = Intent(this, NotificationReceiver::class.java).apply {
            action = "ACTION_PAUSE"
        }
        val stopIntent = Intent(this, NotificationReceiver::class.java).apply {
            action = "ACTION_STOP"
        }

        val pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val stopPending = PendingIntent.getBroadcast(this, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Calmly")
            .setContentText("Playing relaxing sound")
            .setSmallIcon(R.drawable.ic_music)
            .addAction(R.drawable.ic_pause, "Pause/Play", pausePending)
            .addAction(R.drawable.ic_stop, "Stop", stopPending)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    fun togglePause() {
        mediaPlayer?.let {
            if (it.isPlaying) it.pause() else it.start()
        }
    }

    fun stopMedia() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Calmly Player", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}
