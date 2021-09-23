package com.example.googlemaputil_android.api.responses.directions

data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>? = null
)