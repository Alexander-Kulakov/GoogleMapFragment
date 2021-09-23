package com.example.googlemaputil_android.di

import com.example.googlemaputil_core.use_cases.GetDirectionUseCase
import com.example.googlemaputil_core.use_cases.GetInfoByLocationUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single {
        GetDirectionUseCase(get())
    }

    single {
        GetInfoByLocationUseCase(get())
    }
}