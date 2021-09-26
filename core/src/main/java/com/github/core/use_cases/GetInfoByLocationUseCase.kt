package com.github.core.use_cases

import com.github.core.models.place_info.PlaceInfo
import com.github.core.repositories.IGoogleMapApiRepository
import io.reactivex.Single

class GetInfoByLocationUseCase(private val googleMapApiRepository: IGoogleMapApiRepository) {
    fun invoke(placeId: String, language: String?): Single<PlaceInfo> {
        return googleMapApiRepository.getInfoByLocation(placeId, language)
    }
}