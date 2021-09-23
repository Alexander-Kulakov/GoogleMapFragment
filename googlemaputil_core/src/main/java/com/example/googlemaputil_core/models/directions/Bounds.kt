package com.example.googlemaputil_core.models.directions

import com.example.googlemaputil_core.models.Location

data class Bounds(
    val northeast: Location,
    val southwest: Location
)