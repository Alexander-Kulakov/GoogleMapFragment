package com.example.googlemaputil_android.api.responses.place_info

import com.example.googlemaputil_android.api.responses.Location

data class Geometry(
    val location: Location,
    val viewport: Viewport
)