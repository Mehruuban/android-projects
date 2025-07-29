package com.mehru.calmly.util

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RawRes
import com.mehru.calmly.MediaPlaybackService

object PlayController {


    fun start(context: Context, @RawRes soundResId: Int) {
        val i = Intent(context, MediaPlaybackService::class.java).apply {
            action = MediaPlaybackService.ACTION_START
            putExtra(MediaPlaybackService.EXTRA_RES_ID, soundResId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i)
        } else {
            context.startService(i)
        }
    }

    fun toggle(context: Context) {
        val i = Intent(context, MediaPlaybackService::class.java).apply {
            action = MediaPlaybackService.ACTION_TOGGLE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i)
        } else {
            context.startService(i)
        }
    }

    fun stop(context: Context) {
        val i = Intent(context, MediaPlaybackService::class.java).apply {
            action = MediaPlaybackService.ACTION_STOP
        }
        context.startService(i)
    }
}



