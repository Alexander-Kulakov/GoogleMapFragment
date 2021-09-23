package com.example.data.di

import com.example.domain.use_cases.GetDirectionUseCase
import com.example.domain.use_cases.GetInfoByLocationUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single {
        GetDirectionUseCase(get())
    }

    single {
        GetInfoByLocationUseCase(get())
    }
}