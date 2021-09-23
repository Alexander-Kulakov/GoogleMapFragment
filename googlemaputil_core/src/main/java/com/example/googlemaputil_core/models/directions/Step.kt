package com.example.googlemaputil_core.models.directions

import com.example.googlemaputil_core.models.Location

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_location: Location,
    val html_instructions: String,
    val polyline: Polyline,
    val start_location: Location,
    val travel_mode: String
)