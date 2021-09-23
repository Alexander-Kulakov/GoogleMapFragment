package com.example.maps.di

import com.example.googlemaps.GoogleMapVM
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        GoogleMapVM(androidApplication(), get(), get())
    }
}