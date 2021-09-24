package com.example.googlemaps.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.googlemaps.databinding.LayoutInfoWindowBinding
import com.example.googlemaps.extensions.setHtml
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {

    private val binding: LayoutInfoWindowBinding = LayoutInfoWindowBinding.inflate(LayoutInflater.from(context))

    override fun getInfoContents(marker: Marker): View {
        binding.distance.setHtml(marker.title.orEmpty())
        if(!marker.snippet.isNullOrEmpty()) {
            val info = marker.snippet?.split("\n\n")

            if(!info.isNullOrEmpty()) {
                binding.duration.setHtml(info.first().trim())
                if(info.size > 1)
                    binding.instructions.setHtml(info[1].trim())
            }
        }

        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        // TODO Auto-generated method stub
        return null
    }
}