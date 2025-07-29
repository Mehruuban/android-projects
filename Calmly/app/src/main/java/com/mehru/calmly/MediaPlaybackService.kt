package com.mehru.calmly

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MediaPlaybackService : Service() {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_TOGGLE = "ACTION_TOGGLE"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_RES_ID = "EXTRA_RES_ID"
        private const val CHANNEL_ID = "calmly_channel"
        private const val NOTIF_ID = 1001
    }

    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val resId = intent.getIntExtra(EXTRA_RES_ID, 0)
                if (resId != 0) startNewSound(resId)
            }
            ACTION_TOGGLE -> toggle()
            ACTION_STOP -> stopPlaybackAndSelf()
            else -> { /* ignore */ }
        }
        return START_STICKY
    }

    private fun startNewSound(resId: Int) {
        // Always be a foreground service while playing
        createNotificationChannel()
        startForeground(NOTIF_ID, buildNotification(isPlaying = true))

        // Stop any previous sound
        mediaPlayer?.stop()
        mediaPlayer?.release()

        // Start new
        mediaPlayer = MediaPlayer.create(this, resId).apply {
            isLooping = true
            start()
        }
        currentResId = resId

        updateNotification(isPlaying = true)
    }

    private fun toggle() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) mp.pause() else mp.start()
            updateNotification(isPlaying = mp.isPlaying)
        }
    }

    private fun stopPlaybackAndSelf() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification(isPlaying: Boolean) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID, buildNotification(isPlaying))
    }

    private fun buildNotification(isPlaying: Boolean): Notification {
        // Open app when notification tapped
        val openApp = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Actions call back into THIS service
        val togglePI = PendingIntent.getService(
            this, 1,
            Intent(this, MediaPlaybackService::class.java).apply { action = ACTION_TOGGLE },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopPI = PendingIntent.getService(
            this, 2,
            Intent(this, MediaPlaybackService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music) // make sure this exists
            .setContentTitle("Calmly")
            .setContentText(if (isPlaying) "Playing relaxing sound…" else "Paused")
            .setContentIntent(openApp)
            .setOngoing(isPlaying) // persistent while playing
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                if (isPlaying) "Pause" else "Play",
                togglePI
            )
            .addAction(R.drawable.ic_stop, "Stop", stopPI)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID, "Calmly Playback", NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(ch)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}
