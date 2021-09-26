package com.github.googlemapfragment.android.api.responses.place_info

import com.github.googlemapfragment.android.api.responses.Location

data class Geometry(
    val location: Location,
    val viewport: Viewport
)