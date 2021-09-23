package com.example.googlemaputil_android.api.responses.directions

import com.example.googlemaputil_android.api.responses.Location

data class Bounds(
    val northeast: Location,
    val southwest: Location
)