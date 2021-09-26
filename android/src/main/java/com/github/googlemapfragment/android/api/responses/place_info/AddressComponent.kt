package com.github.googlemapfragment.android.api.responses.place_info

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)