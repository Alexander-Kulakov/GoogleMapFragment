package com.example.googlemaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.annotation.ColorRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.domain.models.Location
import com.example.domain.models.directions.Direction
import com.example.domain.models.directions.Step
import com.example.googlemaps.mappers.fromModel
import com.example.googlemaps.mappers.toModel
import com.example.googlemaps.utils.Utils
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

enum class MAP_MODE {
    DIRECTION, PLACE
}

enum class DIRECTION_MARKER {
    ORIGIN, DESTINATION
}

open class GoogleMapWrapper(
    private val context: Context
) {

    private val compositeDisposable = CompositeDisposable()

    companion object {
        private const val TAG = "GoogleMapWrapper"

        private const val DEFAULT_ZOOM = 15f
        private const val DEFAULT_LOCATION_INTERVAL = 5000L
        private const val DEFAULT_FASTEST_LOCATION_INTERVAL = 3000L

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

    var originMarkerDrawableRes = R.drawable.ic_origin_marker
        set(value) {
            field = value
            val icon = Utils.getBitmapFromVector(context, field)
            origin.value?.setIcon(icon)
        }

    var destinationMarkerDrawableRes = R.drawable.ic_destination_marker
        set(value) {
            field = value
            val icon = Utils.getBitmapFromVector(context, field)
            destination.value?.setIcon(icon)
        }

    private lateinit var googleMap: GoogleMap

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    var currentCameraPosition: Location? = null
        private set

    val placeMarker = BehaviorSubject.create<Marker>()

    val origin = BehaviorSubject.create<Marker>()
    val destination = BehaviorSubject.create<Marker>()

    val currentLocation = BehaviorSubject.create<Location>()

    var currentDirectionMarker = BehaviorSubject.createDefault(DIRECTION_MARKER.DESTINATION)


    private val _directionPolylines = mutableMapOf<Polyline, Marker>()
    val directionPolylines: Map<Polyline, Marker> = _directionPolylines

    private val _stepMap = mutableMapOf<Step, Polyline>()
    val stepMap: Map<Step, Polyline> = _stepMap

    var mapClickHandler: (String) -> Unit = {}

    val mapMode = BehaviorSubject.createDefault(MAP_MODE.PLACE)

    var showPolylineInfoWindow = true

    var infoWindowAdapter: GoogleMap.InfoWindowAdapter? = null
        @SuppressLint("PotentialBehaviorOverride")
        set(value) {
            field = value
            googleMap.setInfoWindowAdapter(field)
        }


    @SuppressLint("PotentialBehaviorOverride")
    fun initMap(googleMap: GoogleMap) {
        compositeDisposable.clear()

        this.googleMap = googleMap
        printInfo()
        if(mapMode.value == MAP_MODE.PLACE) {
            if(placeMarker.value != null)
                createPlaceMarker(placeMarker.value!!.position.toModel())
        } else {
            origin.value?.let { createOriginMarker(it.position.toModel()) }
            destination.value?.let { createDestinationMarker(it.position.toModel()) }
        }
        googleMap.setOnCameraMoveListener {
            currentCameraPosition = this.googleMap.cameraPosition.target.toModel()
        }
        googleMap.setInfoWindowAdapter(infoWindowAdapter/*CustomInfoWindowAdapter(context)*/)

        compositeDisposable.add(
            mapMode.subscribe {
                changeMarkerMode(it)
            }
        )
    }

    fun printInfo() {
        Log.w(TAG, "markerMode: ${mapMode.value}")
        Log.w(TAG, "origin: ${origin.value?.position}")
        Log.w(TAG, "destination: ${destination.value?.position}")
        Log.w(TAG, "placeMarker: ${placeMarker.value?.position}")
    }

    private fun changeMarkerMode(mode: MAP_MODE) {
        val isDirection = mode == MAP_MODE.DIRECTION
        directionPolylines.forEach {
            it.key.isVisible = isDirection
            it.value.isVisible = isDirection
        }
        Log.e(TAG, "markerMode direction: ${isDirection}")
        if(isDirection && placeMarker.value != null) {
            createDestinationMarker(placeMarker.value!!.position.toModel())
        }
        if(isDirection && currentLocation.value != null) {
            createOriginMarker(currentLocation.value!!)
        }
        origin.value?.isVisible = isDirection
        destination.value?.isVisible = isDirection
        directionPolylines.keys.forEach {
            it.isVisible = isDirection
            it.isClickable = isDirection
        }
        printInfo()
    }

    fun moveCamera(location: Location, zoom: Float = DEFAULT_ZOOM) {
        Log.d(
            TAG,
            "moveCamera: moving the camera to: lat: " + location.lat + ", lng: " + location.lng
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location.fromModel(), zoom))
    }

    fun createPlaceMarker(location: Location) {
        placeMarker.value?.remove()

        val newPlaceMarker = createMarker(location, null)

        newPlaceMarker?.let {
            it.isVisible = mapMode.value == MAP_MODE.PLACE
            placeMarker.onNext(it)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun createOriginMarker(location: Location) {
        origin.value?.remove()

        val icon = Utils.getBitmapFromVector(context, originMarkerDrawableRes)
        val newOrigin = createMarker(location, null, icon, null)
        newOrigin?.let {
            it.isVisible = mapMode.value == MAP_MODE.DIRECTION
            origin.onNext(it)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun createDestinationMarker(location: Location) {
        destination.value?.remove()

        val icon = Utils.getBitmapFromVector(context, destinationMarkerDrawableRes)
        val newDestination = createMarker(location, null, icon, null)
        newDestination?.let {
            it.isVisible = mapMode.value == MAP_MODE.DIRECTION
            destination.onNext(it)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun createMarker(location: Location, title: String?, markerIcon: BitmapDescriptor? = null, snippet: String? = null): Marker? {
        val markerOptions = MarkerOptions()
        markerOptions.position(location.fromModel())
        markerOptions.title(title)
        markerOptions.snippet(snippet)
        markerOptions.icon(markerIcon ?: BitmapDescriptorFactory.defaultMarker())
        return googleMap.addMarker(markerOptions)
    }

    @SuppressLint("MissingPermission")
    fun setDefaultSettings(): Boolean {
        if (checkCoarseAndFineLocationPermissions()) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = false
            return true
        }
        return false
    }

    fun checkCoarseAndFineLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun initTouchEvents() {
        googleMap.setOnPolylineClickListener {
            val marker = directionPolylines[it]
            marker?.showInfoWindow()
            if(marker != null) moveCamera(marker.position.toModel(), googleMap.cameraPosition.zoom)
        }

        googleMap.setOnPoiClickListener {

            if(mapMode.value == MAP_MODE.PLACE) {
                createPlaceMarker(it.latLng.toModel())
                mapClickHandler(it.placeId)
            } else {
                if(currentDirectionMarker.value == DIRECTION_MARKER.ORIGIN)
                    createOriginMarker(it.latLng.toModel())
                else
                    createDestinationMarker(it.latLng.toModel())
            }
        }
    }

    fun getAddressByLocation(location: Location): Address? {
        return try {
            val list =  Geocoder(context)
                .getFromLocation(location.lat, location.lng, 1)
            val currentAddress = list.firstOrNull()
            Log.w(TAG, "currentAddress: ${currentAddress?.locale}")
            currentAddress
        } catch (e: Exception) {
            Log.w(TAG, "getAddress exception: ${e}")
            null
        }
    }

    private fun addPolylineToMap(polylineList: List<LatLng>, @ColorRes color: Int): Polyline? {
        val polylineOptions = PolylineOptions()

        polylineOptions.color(ContextCompat.getColor(context, color))
        polylineOptions.width(DEFAULT_POLYLINE_WIDTH)
        polylineOptions.startCap(ButtCap())
        polylineOptions.jointType(JointType.ROUND)
        polylineOptions.clickable(true)
        polylineOptions.addAll(polylineList)

        return googleMap.addPolyline(polylineOptions)
    }

    fun createDirection(direction: Direction,
                        getTitle: ((Step) -> String?)? = null,
                        getSnippet: ((Step) -> String?)? = null,
    ): Map<Step, Polyline>? {
        if(direction.routes.isNullOrEmpty())
            return null

        _stepMap.clear()

        _directionPolylines.forEach {
            it.key.remove()
            it.value.remove()
        }
        _directionPolylines.clear()

        for (route in direction.routes!!) {
            for(leg in route.legs) {
                for((currentColorIndex, step) in leg.steps.orEmpty().withIndex()) {
                    val polylineList = mutableListOf<LatLng>()
                    polylineList.addAll(PolyUtil.decode(step.polyline.points))

                    val polyline = addPolylineToMap(polylineList, colors[currentColorIndex % colors.size])

                    if(polyline != null) {
                        if(showPolylineInfoWindow) {
                            val midPoint = polyline.points[polyline.points.size / 2]

                            val invisibleMarker =
                                BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))

                            val title = getTitle?.let { it(step) }
                            val snippet = getSnippet?.let { it(step) }

                            /*val distance = context.resources.getString(R.string.distance, step.distance.text)
                            val duration = context.resources.getString(R.string.duration, step.duration.text)

                            val marker = createMarker(midPoint.toModel(), distance, invisibleMarker,
                                "${duration}\n\n${step.html_instructions}")*/
                            val marker = createMarker(midPoint.toModel(), title, invisibleMarker, snippet)

                            marker?.let {
                                it.position = midPoint
                                _directionPolylines[polyline] = it
                            }
                        }

                        _stepMap[step] = polyline
                    }
                }
            }
        }

        if(direction.bounds != null) {
            val builder = LatLngBounds.builder()
            builder.include(
                LatLng(
                    direction.bounds!!.northeast.lat,
                    direction.bounds!!.northeast.lng
                )
            )
            builder.include(
                LatLng(
                    direction.bounds!!.southwest.lat,
                    direction.bounds!!.southwest.lng
                )
            )

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
        }

        directionPolylines.keys.forEach {
            it.isVisible = mapMode.value == MAP_MODE.DIRECTION
            it.isClickable = mapMode.value == MAP_MODE.DIRECTION
        }

        return _stepMap
    }

    @SuppressLint("MissingPermission")
    fun observeDeviceLocation() {
        val locationRequest = LocationRequest()
            .setInterval(DEFAULT_LOCATION_INTERVAL)
            .setFastestInterval(DEFAULT_FASTEST_LOCATION_INTERVAL)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true)

        val locationSettingsResponseTask = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnCompleteListener {
            try {
                if (checkCoarseAndFineLocationPermissions()) {

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)
                            val lat = locationResult.lastLocation.latitude
                            val lng = locationResult.lastLocation.longitude

                            val newLocation = LatLng(lat, lng)
                            if(currentLocation.value == null) {
                                moveCamera(newLocation.toModel())
                            }

                            currentLocation.onNext(newLocation.toModel())
                        }
                    }, Looper.getMainLooper())

                }

            } catch (e: ApiException) {
                Log.e(TAG, "getDeviceLocation: SecurityException: " + e.message)
            }
        }
    }

    fun moveCameraToCurrentLocation() {
        if(currentLocation.value != null) moveCamera(currentLocation.value!!)
    }
}