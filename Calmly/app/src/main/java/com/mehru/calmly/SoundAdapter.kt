package com.mehru.calmly

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mehru.calmly.util.PlayController

class SoundAdapter(
    private val context: Context,
    private val soundList: List<SoundModel>
) : RecyclerView.Adapter<SoundAdapter.SoundViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_sound, parent, false)
        return SoundViewHolder(view)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val sound = soundList[position]
        holder.title.text = sound.title
        holder.image.setImageResource(sound.imageResId)
        holder.playPause.setImageResource(
            if (sound.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )

        holder.playPause.setOnClickListener {
            if (!sound.isPlaying) {
                // Start service with this sound
                PlayController.start(context, sound.soundResId)

                // Update UI states: only this one playing
                val prev = currentlyPlayingPosition
                if (prev != -1 && prev != position) {
                    soundList[prev].isPlaying = false
                    notifyItemChanged(prev)
                }
                sound.isPlaying = true
                currentlyPlayingPosition = position
                notifyItemChanged(position)

            } else {
                // Toggle pause/resume
                PlayController.toggle(context)
                // Update just the icon: we don't know final state immediately,
                // but you can optimistically flip it:
                sound.isPlaying = !sound.isPlaying
                notifyItemChanged(position)
            }
        }
    }


    override fun getItemCount(): Int = soundList.size

    private fun stopSound() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    class SoundViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.soundImage)
        val title: TextView = view.findViewById(R.id.soundTitle)
        val playPause: ImageButton = view.findViewById(R.id.playPauseButton)
    }
}
