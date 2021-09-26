package com.github.googlemapfragment.android.api.responses.directions

import com.github.googlemapfragment.android.api.responses.Location

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_location: Location,
    val html_instructions: String,
    val polyline: Polyline,
    val start_location: Location,
    val travel_mode: String
)