package com.example.googlemaputil_core.models.directions

data class Route(
    val legs: List<Leg>,
    val overview_polyline: Polyline,
    val summary: String
)