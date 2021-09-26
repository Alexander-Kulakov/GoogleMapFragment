package com.github.googlemapfragment.android.di

import com.github.googlemapfragment.android.GoogleMapVM
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.loadKoinModules

import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        GoogleMapVM(androidApplication(), get(), get())
    }
}

private val loadModules by lazy {
    loadKoinModules(listOf(networkModule, useCaseModule, viewModelModule))
}

fun inject() = loadModules