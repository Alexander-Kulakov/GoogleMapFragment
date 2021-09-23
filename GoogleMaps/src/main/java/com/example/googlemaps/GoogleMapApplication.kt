package com.example.googlemaps

import android.app.Application
import androidx.annotation.StringRes
import com.example.googlemaputil_android.api.ApiConstants
import com.example.googlemaputil_android.di.networkModule
import com.example.googlemaputil_android.di.useCaseModule
import com.example.maps.di.viewModelModule
import com.google.android.libraries.places.api.Places
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

open class GoogleMapApplication(@StringRes private val googleMapKey: Int): Application() {
    override fun onCreate() {
        super.onCreate()

        val apiKey = getString(googleMapKey)
        ApiConstants.API_KEY = apiKey
        Places.initialize(applicationContext, apiKey)

        startKoin {
            androidContext(this@GoogleMapApplication)
            modules(networkModule, useCaseModule, viewModelModule)
        }
    }
}