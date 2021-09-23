package com.example.googlemaputil_core.use_cases

import com.example.googlemaputil_core.common.DIRECTION_TYPE
import com.example.googlemaputil_core.models.Location
import com.example.googlemaputil_core.models.directions.Direction
import com.example.googlemaputil_core.repositories.IGoogleMapApiRepository
import io.reactivex.Single

class GetDirectionUseCase(private val googleMapApiRepository: IGoogleMapApiRepository) {
    fun invoke(origin: Location, destination: Location, directionType: DIRECTION_TYPE, language: String?): Single<Direction> {
        return googleMapApiRepository.getDirection(origin, destination, directionType, language)
    }
}