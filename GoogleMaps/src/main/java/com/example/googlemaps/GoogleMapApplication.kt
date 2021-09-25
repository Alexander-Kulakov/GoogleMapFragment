package com.example.googlemaps

import android.app.Application
import androidx.annotation.StringRes
import com.example.googlemaputil_android.api.ApiConstants
import com.google.android.libraries.places.api.Places

open class GoogleMapApplication(@StringRes private val googleMapKey: Int): Application() {
    override fun onCreate() {
        super.onCreate()

        val apiKey = getString(googleMapKey)
        ApiConstants.API_KEY = apiKey
        Places.initialize(applicationContext, apiKey)
    }
}