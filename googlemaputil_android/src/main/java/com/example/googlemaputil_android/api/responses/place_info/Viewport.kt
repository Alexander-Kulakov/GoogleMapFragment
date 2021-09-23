package com.example.googlemaputil_android.api.responses.place_info

import com.example.googlemaputil_android.api.responses.Location

data class Viewport(
    val northeast: Location,
    val southwest: Location
)