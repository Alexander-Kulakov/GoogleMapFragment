package com.github.googlemapfragment.android.api.responses.directions

import com.github.googlemapfragment.android.api.responses.Location

data class Bounds(
    val northeast: Location,
    val southwest: Location
)