package com.github.googlemapfragment.android.api.responses.directions

import com.github.googlemapfragment.android.api.responses.Location

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val end_address: String? = null,
    val end_location: Location,
    val start_location: Location,
    val start_address: String? = null,
    val steps: List<Step>? = null,
    val traffic_speed_entry: List<Any>? = null,
    val via_waypoint: List<Any>? = null
)