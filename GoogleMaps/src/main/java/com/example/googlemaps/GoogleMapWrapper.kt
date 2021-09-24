package com.example.googlemaps


import android.content.Context


class GoogleMapUtil(
    private val context: Context
) {
    enum class MAP_MODE {
        DIRECTION, PLACE
    }

    enum class DIRECTION_MARKER {
        ORIGIN, DESTINATION
    }
}