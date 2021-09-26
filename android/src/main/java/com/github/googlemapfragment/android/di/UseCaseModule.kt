package com.github.googlemapfragment.android.di

import com.github.core.use_cases.GetDirectionUseCase
import com.github.core.use_cases.GetInfoByLocationUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single {
        GetDirectionUseCase(get())
    }

    single {
        GetInfoByLocationUseCase(get())
    }
}