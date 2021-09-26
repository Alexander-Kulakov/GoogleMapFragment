package com.github.googlemapfragment.android.models

import com.github.core.models.directions.Step
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

data class DirectionSegment(
    val step: Step,
    val polylineOptions: PolylineOptions,
    val markerOptions: MarkerOptions
)

data class DirectionSegmentUI(
    val step: Step,
    val polyline: Polyline,
    val marker: Marker
)