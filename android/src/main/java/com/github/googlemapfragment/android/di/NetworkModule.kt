package com.github.googlemapfragment.android.di

import com.github.googlemapfragment.android.api.ApiConstants
import com.github.googlemapfragment.android.api.GoogleMapApiService
import com.github.googlemapfragment.android.repositories.GoogleMapsApiRepository
import com.github.core.repositories.IGoogleMapApiRepository
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