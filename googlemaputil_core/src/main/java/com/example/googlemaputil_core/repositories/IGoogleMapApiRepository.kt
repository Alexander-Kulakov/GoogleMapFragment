package com.example.googlemaputil_core.repositories

import com.example.googlemaputil_core.common.DIRECTION_TYPE
import com.example.googlemaputil_core.models.Location
import com.example.googlemaputil_core.models.directions.Direction
import com.example.googlemaputil_core.models.place_info.PlaceInfo
import io.reactivex.Single

interface IGoogleMapApiRepository {
    fun getInfoByLocation(placeId: String, language: String?): Single<PlaceInfo>
    fun getDirection(origin: Location, destination: Location, directionType: DIRECTION_TYPE, language: String?): Single<Direction>
}