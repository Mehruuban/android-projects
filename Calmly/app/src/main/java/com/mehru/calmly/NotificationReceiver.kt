package com.mehru.calmly

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "ACTION_PAUSE" -> {
                val service = MediaPlaybackService()
                service.togglePause()  // Note: for proper handling, you should use Messenger/Binding or singleton
            }
            "ACTION_STOP" -> {
                val stopIntent = Intent(context, MediaPlaybackService::class.java)
                context?.stopService(stopIntent)
            }
        }
    }
}
