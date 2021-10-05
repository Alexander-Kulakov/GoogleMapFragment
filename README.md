# GoogleMapFragment [![](https://jitpack.io/v/Alexander-Kulakov/GoogleMapFragment.svg)](https://jitpack.io/#Alexander-Kulakov/GoogleMapFragment)
this library allow to simplify working with google maps sdk for android.

## Demo
See demo project here [here](https://src.vironit.com/a.kulakov/maps)

![image](https://user-images.githubusercontent.com/90984417/135409254-f5987ed9-831e-4803-bf35-6e05473966ae.png)
![image](https://user-images.githubusercontent.com/90984417/135409328-9d643878-694e-44c0-8ee4-ac782c90b99d.png)
![image](https://user-images.githubusercontent.com/90984417/135409392-c9f6ebd8-9b54-4624-997b-3d2d1857ce09.png)
![image](https://user-images.githubusercontent.com/90984417/135409469-eea620f5-557e-4580-8e34-b0a375f5f44e.png)


## How can I use it?
This library contains 3 modules - core, android and GoogleMapFragment. 
* "Core" contains models and interfaces. 
* "Android" contains implementations for android. 
* "GoogleMapFragment" is main android module with nessasary functionality.
1. Paste it in your app module:
```
implementation 'com.github.Alexander-Kulakov.GoogleMapFragment:GoogleMaps:x.y.z'
implementation 'com.github.Alexander-Kulakov.GoogleMapFragment:core:x.y.z'
```

2. Create your application class and inherite it from "GoogleMapApplication", in the constructor pass string resource with your google map api key:
```kotlin
class MapsApplication: GoogleMapApplication(R.string.google_maps_key) {
    override fun onCreate() {
        super.onCreate()
        // your code
    }
}
```

3. Inherite your fragment with SupportMapFragment from "GoogleMapsFragment", pass into constructor id of SupportMapFragment
```kotlin
class MainFragment: GoogleMapFragment(R.id.map), IMyLocationChangedListener, IDirectionListener, IMapModeChangedListener, IPlaceMarkerChangedListener
        IDirectionMarkersChangedListener, IPlaceInfoStatusChangedListener {
   
    // override this method if you need googleMap object
    override fun onMapReady(googleMap: GoogleMap) {
      
      // set listeners here
      myLocationChangedListener = this
      directionListener = this
      placeInfoStatusChangedListener = this
      mapModeChangedListener = this
      placeMarkerChangedListener = this
      directionMarkersChangedListener = this
      
      super.onMapReady() // be sure that you call this method in base class because this library requests permissions and subscribes to data changes
    }

    override fun onPlaceInfoStatusChange(placeInfoResult: Result<PlaceInfo>) {
      // if the user taps on the point of interest, information fetched automatically about it. You can trigger fetching place info calling "" 
      when(placeInfoResult) {
            is Result.Loading -> {
                // place info is loading
            }
            is Result.Success -> {
                // place info is loaded successfully
            }
            is Result.Failure -> {
                // place info is loaded unsuccessfully
            }
        }
    }

    override fun onDirectionChange(directionResult: Result<Direction>) {
      // this callback calls if we calls the method getDirection() in the base class
    }

    override fun onCurrentLocationChange(latLng: LatLng) {
      // calls this method if current user location changes
    }

    override fun onCurrentAddressChange(address: Address) {
      // calls in the same time if currentLocationChanged is calling and pass current user address
    }

    override fun onDirectionRender(directionsSegments: List<DirectionSegmentUI>) {
      // calls if your direction is received and rendered on the map
    }

    // calls if your marker positions are changed
    override fun onOriginLocationChange(latLng: LatLng?)
    override fun onDestinationLocationChange(latLng: LatLng?)
    override fun onPlaceMarkerChange(latLng: LatLng?)

    override fun onDirectionMarkerTypeChange(directionMarker: DIRECTION_MARKER) {
      // calls if your direction marker type is changed (ORIGIN or DESTINATION)
    }
    
    override fun onMapModeChange(mapMode: MAP_MODE) {
      // calls if your map mode is changed (PLACE or DIRECTION)
    }
}
```
## Mehods description
Method/Property | Description
------------ | -------------
moveCamera(location: LatLng, zoom: Float = DEFAULT_ZOOM) | move camera to target position and zoom
moveToCurrentLocation() | move camera to current user location
toggleMapMode() | toggle map mode (PLACE and DIRECTION)
getPlaceInfo(placeId: String) | fetch information about place by id
getDirection(dirType: DIRECTION_TYPE = DIRECTION_TYPE.DRIVING) | fetch direction with target direction type (DRIVING, WALKING, BICYCLING, TRANSIT)
findSegmentByStep(step: Step): DirectionSegmentUI? | find segment of your direction, if it isn't found or direction isn't received, return null
setMarkerByPlace(placeId: String, latLng: LatLng) | set marker according current map mode and direction marker type
isCoarseAndFineLocationPermissionsGranted(): Boolean | check that fine and coarse permissions are granted
getDirectionMarkerType(): DIRECTION_MARKER | return current direction marker type
setDirectionMarkerType(directionMarker: DIRECTION_MARKER) | set direction marker type
getMapMode(): MAP_MODE | return current map mode
setMapMode(mapMode: MAP_MODE) | set current map mode
infoWindowAdapter: GoogleMap.InfoWindowAdapter? | set info window adapter
isDirectionBuildingAvailable: Boolean | check that all data is received for fetching direction
myLocationSynchronizedWithOrigin: Boolean | syncronize your origin marker position with user position (default - false)
