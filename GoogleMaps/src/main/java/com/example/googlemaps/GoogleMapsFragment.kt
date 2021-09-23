package com.example.googlemaps

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


open class GoogleMapsFragment(@IdRes private val mapFragmentId: Int)
    : Fragment(), OnMapReadyCallback {

    companion object {
        private const val TAG = "GoogleMapsFragment"
    }

    protected val googleMapViewModel by sharedViewModel<GoogleMapVM>()

    protected val compositeDisposable = CompositeDisposable()

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

        if(googleMapViewModel.googleMapWrapper.checkCoarseAndFineLocationPermissions()) {
            initMap()
        } else {
            locationPermissionResult.launch(permissions)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMapViewModel.googleMapWrapper.initMap(googleMap)

        if (googleMapViewModel.googleMapWrapper.checkCoarseAndFineLocationPermissions()) {
            googleMapViewModel.googleMapWrapper.setDefaultSettings()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}