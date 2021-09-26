package com.github.core.use_cases

import com.github.core.common.DIRECTION_TYPE
import com.github.core.models.Location
import com.github.core.models.directions.Direction
import com.github.core.repositories.IGoogleMapApiRepository
import io.reactivex.Single

class GetDirectionUseCase(private val googleMapApiRepository: IGoogleMapApiRepository) {
    fun invoke(origin: Location, destination: Location, directionType: DIRECTION_TYPE, language: String?): Single<Direction> {
        return googleMapApiRepository.getDirection(origin, destination, directionType, language)
    }
}