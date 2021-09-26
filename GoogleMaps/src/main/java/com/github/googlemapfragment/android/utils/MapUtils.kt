package com.github.googlemapfragment.android.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng

object MapUtils {
    const val TAG = "MapUtils"

    fun getAddressByLocation(context: Context, location: LatLng): Address? {
        return try {
            val list =  Geocoder(context)
                .getFromLocation(location.latitude, location.longitude, 1)
            val currentAddress = list.firstOrNull()
            Log.w(TAG, "currentAddress: ${currentAddress?.locale}")
            currentAddress
        } catch (e: Exception) {
            Log.w(TAG, "getAddress exception: ${e}")
            null
        }
    }
}