package com.example.googlemaputil_core.use_cases

import com.example.googlemaputil_core.models.place_info.PlaceInfo
import com.example.googlemaputil_core.repositories.IGoogleMapApiRepository
import io.reactivex.Single

class GetInfoByLocationUseCase(private val googleMapApiRepository: IGoogleMapApiRepository) {
    fun invoke(placeId: String, language: String?): Single<PlaceInfo> {
        return googleMapApiRepository.getInfoByLocation(placeId, language)
    }
}