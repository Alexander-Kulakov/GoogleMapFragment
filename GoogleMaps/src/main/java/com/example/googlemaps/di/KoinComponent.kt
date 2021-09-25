package com.example.googlemaps.di

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent

internal object MyKoinContext {
    lateinit var koinApplication: KoinApplication
}

interface MyKoinComponent : KoinComponent {
    override fun getKoin(): Koin {
        return MyKoinContext.koinApplication.koin
    }
}