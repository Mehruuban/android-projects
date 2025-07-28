package com.mehru.calmly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SleepFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var soundList: ArrayList<SoundModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sleep, container, false)

        recyclerView = view.findViewById(R.id.soundsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        soundList = arrayListOf(
            SoundModel("White Noise", R.raw.white_noise, R.drawable.noise,false),
            SoundModel("Lullaby", R.raw.lullaby, R.drawable.lullying,false),
            SoundModel("Fan", R.raw.fan, R.drawable.fanning,false),
            SoundModel("Fan", R.raw.fan, R.drawable.fanning,false),
            SoundModel("Deep Hum", R.raw.deephum, R.drawable.humming,false)
        )

        val adapter = SoundAdapter(requireContext(), soundList)
        recyclerView.adapter = adapter

        return view
    }
}
