package com.example.googlemaputil_android.api.responses.directions

data class DirectionResponse(
    val bounds: Bounds? = null,
    val geocoded_waypoints: List<GeocodedWaypoint>? = null,
    val legs: List<Leg>? = null,
    val routes: List<Route>? = null,
    val status: String
)