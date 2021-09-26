package com.github.core.models.directions

import com.github.core.models.Location

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val end_address: String? = null,
    val end_location: Location,
    val start_location: Location,
    val start_address: String? = null,
    val steps: List<Step>? = null,
)