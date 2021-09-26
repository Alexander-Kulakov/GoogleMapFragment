package com.github.core.repositories

import com.github.core.common.DIRECTION_TYPE
import com.github.core.models.Location
import com.github.core.models.directions.Direction
import com.github.core.models.place_info.PlaceInfo
import io.reactivex.Single

interface IGoogleMapApiRepository {
    fun getInfoByLocation(placeId: String, language: String?): Single<PlaceInfo>
    fun getDirection(origin: Location, destination: Location, directionType: DIRECTION_TYPE, language: String?): Single<Direction>
}