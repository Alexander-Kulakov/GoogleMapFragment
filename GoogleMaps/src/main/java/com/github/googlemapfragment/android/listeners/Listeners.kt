package com.github.googlemapfragment.android.listeners

import android.location.Address
import com.github.core.common.DIRECTION_MARKER
import com.github.core.common.MAP_MODE
import com.github.core.common.Result
import com.github.core.models.directions.Direction
import com.github.core.models.place_info.PlaceInfo
import com.github.googlemapfragment.android.models.DirectionSegmentUI
import com.google.android.gms.maps.model.LatLng

interface IMyLocationChangedListener {
    fun onCurrentLocationChange(latLng: LatLng)
    fun onCurrentAddressChange(address: Address)
}

interface IDirectionListener {
    fun onDirectionChange(directionResult: Result<Direction>)
    fun onDirectionRender(directionsSegments: List<DirectionSegmentUI>)
}

fun interface IPlaceInfoStatusChangedListener {
    fun onChange(placeInfoResult: Result<PlaceInfo>)
}

interface IMapModeChangedListener {
    fun onMapModeChange(mapMode: MAP_MODE)
    fun onDirectionMarkerTypeChange(directionMarker: DIRECTION_MARKER)
}

fun interface IPlaceMarkerChangedListener {
    fun onChange(latLng: LatLng?)
}
interface IDirectionMarkersChangedListener {
    fun onOriginLocationChange(latLng: LatLng?)
    fun onDestinationLocationChange(latLng: LatLng?)
}
