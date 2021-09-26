package com.github.googlemapfragment.android.api.responses.place_info

import com.github.googlemapfragment.android.api.responses.Location

data class Viewport(
    val northeast: Location,
    val southwest: Location
)