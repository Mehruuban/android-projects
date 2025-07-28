package com.mehru.calmly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MeditationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var soundList: ArrayList<SoundModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meditaion, container, false)

        recyclerView = view.findViewById(R.id.rvMeditation)
        recyclerView.layoutManager = LinearLayoutManager(context)

        soundList = arrayListOf(
            SoundModel("Forest", R.raw.forest, R.drawable.fores,false),
            SoundModel("Rain", R.raw.rain, R.drawable.rainy,false),
            SoundModel("Campfire", R.raw.campfire, R.drawable.camp,false),
            SoundModel("Ocean", R.raw.ocean, R.drawable.ocea,false),
            SoundModel("Rain", R.raw.rain, R.drawable.rainy,false)
        )

        val adapter = SoundAdapter(requireContext(), soundList)
        recyclerView.adapter = adapter

        return view
    }
}
