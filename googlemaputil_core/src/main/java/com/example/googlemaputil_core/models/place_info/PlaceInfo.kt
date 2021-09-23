package com.example.googlemaputil_core.models.place_info

import com.example.googlemaputil_core.models.Location

data class PlaceInfo(
    val address: String? = null,
    val phoneNumber: String? = null,
    val name: String? = null,
    val openingHours: OpeningHours? = null,
    val photos: List<Photo>? = null,
    val placeId: String? = null,
    val rating: Double? = null,
    val reviews: List<Review>? = null,
    val types: List<String>? = null,
    val url: String? = null,
    val userRatingsTotal: Int? = null,
    val utcOffset: Int? = null,
    val website: String? = null,
    val location: Location? = null
)