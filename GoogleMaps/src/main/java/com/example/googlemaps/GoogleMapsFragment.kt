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
import com.example.googlemaps.di.inject
import com.example.googlemaps.models.DirectionSegmentUI
import com.example.googlemaputil_core.common.DIRECTION_MARKER
import com.example.googlemaputil_core.common.DIRECTION_TYPE
import com.example.googlemaputil_core.common.MAP_MODE
import com.example.googlemaputil_core.common.Result
import com.example.googlemaputil_core.models.directions.Direction
import com.example.googlemaputil_core.models.directions.Step
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
    }

    private val googleMapViewModel by sharedViewModel<GoogleMapVM>()

    private val compositeDisposable = CompositeDisposable()

    private lateinit var googleMap: GoogleMap

    private var destinationMarker: Marker? = null

    private var originMarker: Marker? = null

    private var placeMarker: Marker? = null

    private val directionSegmentsUI = mutableListOf<DirectionSegmentUI>()


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestLocationPermission()
    }

    private fun initMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(mapFragmentId) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun requestLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions")
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if(isCoarseAndFineLocationPermissionsGranted()) {
            initMap()
        } else {
            locationPermissionResult.launch(permissions)
        }
    }

    @SuppressLint("MissingPermission", "PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        compositeDisposable.addAll(
            googleMapViewModel.infoWindowAdapter.subscribe {
                googleMap.setInfoWindowAdapter(it)
            },
            googleMapViewModel.currentCameraPosition.subscribe {
                moveCamera(it)
            },
            googleMapViewModel.originMarker.subscribe({
                originMarker?.remove()
                originMarker = googleMap.addMarker(it)
                originLocationChanged(originMarker?.position)
            }, {}),
            googleMapViewModel.destinationMarker.subscribe({
                destinationMarker?.remove()
                destinationMarker = googleMap.addMarker(it)
                destinationLocationChanged(destinationMarker?.position)
            }, {}),
            googleMapViewModel.placeMarker.subscribe({
                placeMarker?.remove()
                placeMarker = googleMap.addMarker(it)
            }, {}),
            googleMapViewModel.placeInfo.subscribe {
                placeInfoChanged(it)
            },
            googleMapViewModel.direction.subscribe {
                directionChanged(it)
            },
            googleMapViewModel.currentLocation.subscribe {
                currentLocationChanged(it)
            },
            googleMapViewModel.currentAddress.subscribe {
                currentAddressChanged(it)
            },
            googleMapViewModel.currentMapMode.subscribe {
                val isPlace = it == MAP_MODE.PLACE
                directionSegmentsUI.forEach {
                    it.polyline.isVisible = !isPlace
                    it.polyline.isClickable = !isPlace
                    it.marker.isVisible = !isPlace
                    if(isPlace) it.marker.hideInfoWindow()
                }
                placeMarker?.isVisible = isPlace
                originMarker?.isVisible = !isPlace
                destinationMarker?.isVisible = !isPlace
                mapModeChanged(it)
            },
            googleMapViewModel.currentDirectionMarkerType.subscribe {
                directionMarkerTypeChanged()
            },
            googleMapViewModel.directionSegments.subscribe {
                directionSegmentsUI.forEach {
                    it.marker.remove()
                    it.polyline.remove()
                }
                directionSegmentsUI.clear()

                it.forEach {
                    val marker = googleMap.addMarker(it.markerOptions)
                    val polyline = googleMap.addPolyline(it.polylineOptions)
                    directionSegmentsUI.add(
                        DirectionSegmentUI(it.step, polyline, marker)
                    )
                }

                directionRendered(directionSegmentsUI)
            }
        )

        googleMap.setOnPoiClickListener {
            googleMapViewModel.setMarker(it.placeId, it.latLng)
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
        if(myLocation != null)
            googleMapViewModel.currentCameraPosition.onNext(myLocation)
    }

    fun toggleMapMode() {
        googleMapViewModel.toggleMapMode()
    }

    fun getDirection(dirType: DIRECTION_TYPE = DIRECTION_TYPE.DRIVING) {
        googleMapViewModel.getDirection(dirType)
    }

    fun findSegmentByStep(step: Step): DirectionSegmentUI? {
        for(segment in directionSegmentsUI)
            if(segment.step == step)
                return segment
        return null
    }

    fun setMarkerByPlace(placeId: String, latLng: LatLng) {
        googleMapViewModel.setMarker(placeId, latLng)
    }

    fun isCoarseAndFineLocationPermissionsGranted()
        = googleMapViewModel.isCoarseAndFineLocationPermissionsGranted()

    /*var currentDirectionMarkerType = DIRECTION_MARKER.DESTINATION
        get() = googleMapViewModel.currentDirectionMarkerType.value ?: GoogleMapVM.DEFAULT_DIRECTION_MARKER_TYPE
        set(value) {
            field = value
            googleMapViewModel.currentDirectionMarkerType.onNext(field)
        }*/

    fun getDirectionMarkerType()
        = googleMapViewModel.currentDirectionMarkerType.value ?: GoogleMapVM.DEFAULT_DIRECTION_MARKER_TYPE

    fun setDirectionMarkerType(directionMarker: DIRECTION_MARKER) {
        googleMapViewModel.currentDirectionMarkerType.onNext(directionMarker)
    }

    fun getMapMode() = googleMapViewModel.currentMapMode.value ?: GoogleMapVM.DEFAULT_MAP_MODE

    fun setMapMode(mapMode: MAP_MODE) {
        googleMapViewModel.currentMapMode.onNext(mapMode)
    }

    /*var mapMode = MAP_MODE.PLACE
        get() = googleMapViewModel.currentMapMode.value ?: GoogleMapVM.DEFAULT_MAP_MODE
        set(value) {
            field = value
            googleMapViewModel.currentMapMode.onNext(field)
        }*/

    var infoWindowAdapter: GoogleMap.InfoWindowAdapter? = null
        set(value) {
            field = value
            if(field != null)
                googleMapViewModel.infoWindowAdapter.onNext(field!!)
        }

    val isDirectionBuildingAvailable: Boolean
        get() = originMarker != null && destinationMarker != null


    abstract fun mapModeChanged(mapMode: MAP_MODE)

    abstract fun placeInfoChanged(placeInfoResult: Result<PlaceInfo>)

    abstract fun directionChanged(directionResult: Result<Direction>)

    abstract fun currentLocationChanged(latLng: LatLng)

    abstract fun currentAddressChanged(address: Address)

    abstract fun directionRendered(directionsSegments: List<DirectionSegmentUI>)

    abstract fun originLocationChanged(latLng: LatLng?)
    abstract fun destinationLocationChanged(latLng: LatLng?)

    abstract fun directionMarkerTypeChanged()

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}