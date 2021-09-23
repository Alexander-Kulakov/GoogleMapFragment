package com.example.googlemaputil_android.api.responses.directions

import com.example.googlemaputil_android.api.responses.Location

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_location: Location,
    val html_instructions: String,
    val polyline: Polyline,
    val start_location: Location,
    val travel_mode: String
)