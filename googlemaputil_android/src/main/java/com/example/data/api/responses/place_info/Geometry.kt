package com.example.data.api.responses.place_info

import com.example.data.api.responses.Location

data class Geometry(
    val location: Location,
    val viewport: Viewport
)