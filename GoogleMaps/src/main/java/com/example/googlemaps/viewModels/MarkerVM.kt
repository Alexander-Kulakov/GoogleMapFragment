package com.example.googlemaps.viewModels

import com.example.googlemaps.GoogleMapUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import io.reactivex.subjects.BehaviorSubject

class MarkerVM() {
    var marker: Marker? = null
        private set

    val visible = BehaviorSubject.createDefault(true)
    val position = BehaviorSubject.create<Marker>()
}

class MapsVM() {

    val mapMode = GoogleMapUtil.MAP_MODE.PLACE

    val placeMarker = BehaviorSubject.create<MarkerOptions>()
    val originMarker = BehaviorSubject.create<MarkerOptions>()
    val destinationMarker = BehaviorSubject.create<MarkerOptions>()

    fun clickMap(value: PointOfInterest) {

    }
}

class M {
    private lateinit var googleMap: GoogleMap
    private val vm = MapsVM()

    private var destinationMarker: Marker? = null

    private fun init() {
        val s = vm.destinationMarker.subscribe {
            if(destinationMarker != null)
                destinationMarker!!.position = it.position
            else
                destinationMarker = googleMap.addMarker(it)
        }

        googleMap.setOnPoiClickListener {
            vm.clickMap(it)
        }
    }
}