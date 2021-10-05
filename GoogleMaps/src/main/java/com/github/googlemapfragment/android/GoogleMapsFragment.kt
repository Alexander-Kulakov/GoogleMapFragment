package com.github.googlemapfragment.android

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.github.core.common.DIRECTION_MARKER
import com.github.core.common.DIRECTION_TYPE
import com.github.core.common.MAP_MODE
import com.github.core.models.directions.Step
import com.github.googlemapfragment.android.di.inject
import com.github.googlemapfragment.android.listeners.*
import com.github.googlemapfragment.android.models.DirectionSegmentUI
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
            googleMapViewModel.originMarker.subscribe({
                originMarker?.remove()
                originMarker = googleMap.addMarker(it)
                originMarker?.isVisible = getMapMode() == MAP_MODE.DIRECTION
                directionMarkersChangedListener?.onOriginLocationChange(originMarker?.position)
            }, {}),
            googleMapViewModel.destinationMarker.subscribe({
                destinationMarker?.remove()
                destinationMarker = googleMap.addMarker(it)
                destinationMarker?.isVisible = getMapMode() == MAP_MODE.DIRECTION
                directionMarkersChangedListener?.onDestinationLocationChange(destinationMarker?.position)
            }, {}),
            googleMapViewModel.placeMarker.subscribe({
                placeMarker?.remove()
                placeMarker = googleMap.addMarker(it)
                placeMarker?.isVisible = getMapMode() == MAP_MODE.PLACE
                placeMarkerChangedListener?.onPlaceMarkerChange(placeMarker?.position)
            }, {}),
            googleMapViewModel.placeInfo.subscribe {
                placeInfoStatusChangedListener?.onPlaceInfoStatusChange(it)
            },
            googleMapViewModel.direction.subscribe {
                directionListener?.onDirectionChange(it)
            },
            googleMapViewModel.currentLocation.subscribe {
                myLocationChangedListener?.onCurrentLocationChange(it)
            },
            googleMapViewModel.currentAddress.subscribe {
                myLocationChangedListener?.onCurrentAddressChange(it)
            },
            googleMapViewModel.currentMapMode.subscribe {
                val isPlace = it == MAP_MODE.PLACE
                updateDirectionVisibility()
                placeMarker?.isVisible = isPlace
                originMarker?.isVisible = !isPlace
                destinationMarker?.isVisible = !isPlace
                mapModeChangedListener?.onMapModeChange(it)
            },
            googleMapViewModel.currentDirectionMarkerType.subscribe {
                mapModeChangedListener?.onDirectionMarkerTypeChange(it)
            },
            googleMapViewModel.directionSegments.subscribe {
                Log.w(TAG, "directionSegments rendered")

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

                updateDirectionVisibility()

                directionListener?.onDirectionRender(directionSegmentsUI)
            }
        )

        googleMapViewModel.cameraPosition?.let {
            moveCamera(it.target, it.zoom)
        }

        googleMap.setOnPoiClickListener {
            googleMapViewModel.setMarker(it.placeId, it.latLng)
        }

        googleMap.setOnCameraMoveListener {
            googleMapViewModel.cameraPosition = googleMap.cameraPosition
        }

        googleMap.setOnPolylineClickListener { polyline ->
            val segment = directionSegmentsUI.find { it.polyline == polyline }
            if(segment != null) {
                segment.marker.showInfoWindow()
                moveCamera(segment.marker.position)
            }
        }
    }

    private fun updateDirectionVisibility() {
        val isPlace = getMapMode() == MAP_MODE.PLACE
        directionSegmentsUI.forEach {
            it.polyline.isVisible = !isPlace
            it.polyline.isClickable = !isPlace
            it.marker.isVisible = !isPlace
            if(isPlace) it.marker.hideInfoWindow()
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
            moveCamera(myLocation)
    }

    fun toggleMapMode() {
        googleMapViewModel.toggleMapMode()
    }

    fun getPlaceInfo(placeId: String) {
        googleMapViewModel.getInfoByLocation(placeId)
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

    fun getDirectionMarkerType()
        = googleMapViewModel.currentDirectionMarkerType.value ?: GoogleMapVM.DEFAULT_DIRECTION_MARKER_TYPE

    fun setDirectionMarkerType(directionMarker: DIRECTION_MARKER) {
        googleMapViewModel.currentDirectionMarkerType.onNext(directionMarker)
    }

    fun getMapMode() = googleMapViewModel.currentMapMode.value ?: GoogleMapVM.DEFAULT_MAP_MODE

    fun setMapMode(mapMode: MAP_MODE) {
        googleMapViewModel.currentMapMode.onNext(mapMode)
    }

    var infoWindowAdapter: GoogleMap.InfoWindowAdapter? = null
        set(value) {
            field = value
            if(field != null)
                googleMapViewModel.infoWindowAdapter.onNext(field!!)
        }

    val isDirectionBuildingAvailable: Boolean
        get() = originMarker != null && destinationMarker != null


    var myLocationSynchronizedWithOrigin = false
        set(value) {
            field = value
            googleMapViewModel.myLocationSynchronizedWithOrigin = field
        }


    var myLocationChangedListener: IMyLocationChangedListener? = null
    var directionListener: IDirectionListener? = null
    var placeInfoStatusChangedListener: IPlaceInfoStatusChangedListener? = null
    var mapModeChangedListener: IMapModeChangedListener? = null
    var placeMarkerChangedListener: IPlaceMarkerChangedListener? = null
    var directionMarkersChangedListener: IDirectionMarkersChangedListener? = null

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}