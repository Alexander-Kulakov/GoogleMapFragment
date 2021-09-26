package com.github.core.models.directions

import com.github.core.models.Location

data class Bounds(
    val northeast: Location,
    val southwest: Location
)