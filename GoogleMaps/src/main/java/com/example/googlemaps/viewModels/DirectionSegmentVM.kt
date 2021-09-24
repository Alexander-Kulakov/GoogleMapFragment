package com.example.googlemaps.viewModels

import com.example.googlemaputil_core.models.directions.Step
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

data class DirectionSegment(
    val step: Step,
    val polyline: PolylineOptions,
    val marker: MarkerOptions
)