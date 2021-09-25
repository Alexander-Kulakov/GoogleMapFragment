package com.example.googlemaps

import android.app.Application
import androidx.annotation.StringRes
import com.example.googlemaps.di.MyKoinContext.koinApplication
import com.example.googlemaps.di.viewModelModule
import com.example.googlemaputil_android.api.ApiConstants
import com.example.googlemaputil_android.di.networkModule
import com.example.googlemaputil_android.di.useCaseModule
import com.google.android.libraries.places.api.Places
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication

open class GoogleMapApplication(@StringRes private val googleMapKey: Int): Application() {
    override fun onCreate() {
        super.onCreate()

        val apiKey = getString(googleMapKey)
        ApiConstants.API_KEY = apiKey
        Places.initialize(applicationContext, apiKey)

        /*startKoin {
            androidContext(this@GoogleMapApplication)
            modules(networkModule, useCaseModule, viewModelModule)
        }*/
        koinApplication = koinApplication {
            androidContext(this@GoogleMapApplication)
            modules(networkModule, useCaseModule, viewModelModule)
        }
    }
}