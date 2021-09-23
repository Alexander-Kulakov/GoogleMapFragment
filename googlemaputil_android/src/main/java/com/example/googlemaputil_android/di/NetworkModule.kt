package com.example.googlemaputil_android.di

import com.example.googlemaputil_android.api.ApiConstants
import com.example.googlemaputil_android.api.GoogleMapApiService
import com.example.googlemaputil_android.repositories.GoogleMapsApiRepository
import com.example.googlemaputil_core.repositories.IGoogleMapApiRepository
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GoogleMapApiService::class.java)
    }

    single<IGoogleMapApiRepository> {
        GoogleMapsApiRepository(get())
    }
}