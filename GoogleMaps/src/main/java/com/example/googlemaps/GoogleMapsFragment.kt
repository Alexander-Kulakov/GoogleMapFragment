package com.example.googlemaps

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.example.googlemaputil_core.common.Result
import com.example.googlemaputil_core.models.directions.Direction
import com.example.googlemaputil_core.models.place_info.PlaceInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


abstract class GoogleMapsFragment(@IdRes private val mapFragmentId: Int)
    : Fragment(), OnMapReadyCallback {

    companion object {
        private const val TAG = "GoogleMapsFragment"

        private const val DEFAULT_ZOOM = 15f
        private const val DEFAULT_POLYLINE_WIDTH = 8f

        private val colors = listOf(
            R.color.black,
            R.color.green,
            R.color.red,
            R.color.teal_700,
            R.color.purple_500,
            R.color.orange
        )
    }

    protected val googleMapViewModel by sharedViewModel<GoogleMapVM>()

    protected val compositeDisposable = CompositeDisposable()

    private lateinit var googleMap: GoogleMap

    private val locationPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            Log.d(TAG, "onRequestPermissionsResult: permission granted")
            initMap()
        } else {
            Log.e(TAG, "onRequestPermissionsResult: permission failed")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocationPermission()
    }

    private fun initMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(mapFragmentId) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun checkLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions")
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if(googleMapViewModel.checkCoarseAndFineLocationPermissions()) {
            initMap()
        } else {
            locationPermissionResult.launch(permissions)
        }
    }

    private var destinationMarker: Marker? = null

    private var originMarker: Marker? = null

    private var placeMarker: Marker? = null

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        compositeDisposable.addAll(
            googleMapViewModel.currentCameraPosition.subscribe {
                moveCamera(it)
            },
            googleMapViewModel.originMarker.subscribe {
                originMarker?.remove()
                originMarker = googleMap.addMarker(it)
            },
            googleMapViewModel.destinationMarker.subscribe {
                destinationMarker?.remove()
                destinationMarker = googleMap.addMarker(it)
            },
            googleMapViewModel.placeMarker.subscribe {
                placeMarker?.remove()
                placeMarker = googleMap.addMarker(it)
            },
            googleMapViewModel.placeInfo.subscribe {
                placeInfoLoaded(it)
            },
            googleMapViewModel.direction.subscribe {
                directionLoaded(it)
            },
            googleMapViewModel.currentLocation.subscribe {
                currentLocationChanged(it)
            },
            googleMapViewModel.currentAddress.subscribe {
                currentAddressChanged(it)
            },
            googleMapViewModel.currentMapMode.subscribe {

            }
        )

        googleMap.setOnPoiClickListener {
            googleMapViewModel.setMarkerByPointOfInterest(it)
        }
    }

    fun moveCamera(location: LatLng, zoom: Float = DEFAULT_ZOOM) {
        Log.d(
            TAG,
            "moveCamera: moving the camera to: lat: " + location.latitude + ", lng: " + location.longitude
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
    }

    fun moveToCurrentLocation() {
        val myLocation = googleMapViewModel.currentLocation.value
        if(myLocation != null) moveCamera(myLocation)
    }

    fun toggleMapMode() {
        googleMapViewModel.toggleMapMode()
    }

    fun getDirection() {
        googleMapViewModel.getDirection()
    }

    abstract fun mapModeChanged(mapMode: MAP_MODE)

    abstract fun placeInfoLoaded(placeInfoResult: Result<PlaceInfo>)

    abstract fun directionLoaded(directionResult: Result<Direction>)

    abstract fun currentLocationChanged(latLng: LatLng)

    abstract fun currentAddressChanged(address: Address)






    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}