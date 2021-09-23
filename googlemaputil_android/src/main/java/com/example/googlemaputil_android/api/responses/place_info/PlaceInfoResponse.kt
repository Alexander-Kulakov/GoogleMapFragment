package com.example.googlemaputil_android.api.responses.place_info

data class PlaceInfoResponse(
    val html_attributions: List<String>,
    val result: Result,
    val status: String
)