package com.github.googlemapfragment.android.api.responses.place_info

data class Photo(
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String,
    val width: Int
)