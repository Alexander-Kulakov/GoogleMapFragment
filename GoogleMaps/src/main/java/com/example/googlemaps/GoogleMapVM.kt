package com.example.googlemaps

import android.app.Application
import android.location.Address
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.common.DIRECTION_TYPE
import com.example.domain.common.Result
import com.example.domain.models.Location
import com.example.domain.models.directions.Direction
import com.example.domain.models.place_info.PlaceInfo
import com.example.domain.use_cases.GetDirectionUseCase
import com.example.domain.use_cases.GetInfoByLocationUseCase
import com.example.googlemaps.mappers.toModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

open class GoogleMapVM(
    app: Application,
    private val getInfoByLocationUseCase: GetInfoByLocationUseCase,
    private val getDirectionUseCase: GetDirectionUseCase,
): AndroidViewModel(app) {
    companion object {
        private const val TAG = "GoogleMapViewModel"
    }

    var currentAddress = BehaviorSubject.create<Address>()

    var directionType = DIRECTION_TYPE.DRIVING

    val googleMapWrapper = GoogleMapWrapper(app)
    private val placesClient = Places.createClient(app)

    private val compositeDisposable = CompositeDisposable()

    private val _placeData = MutableLiveData<Result<PlaceInfo>>()
    val placeData: LiveData<Result<PlaceInfo>> = _placeData

    private val _direction = MutableLiveData<Result<Direction>>()
    val direction: LiveData<Result<Direction>> = _direction

    val currentLanguage: String?
        get() = currentAddress.value?.locale?.language


    var currentPlaceId: String? = null
        private set


    init {
        compositeDisposable.add(
            googleMapWrapper.currentLocation.subscribe {
                val address = googleMapWrapper.getAddressByLocation(it) ?: return@subscribe
                currentAddress.onNext(address)
            }
        )
    }

    fun toggleMapMode() {
        googleMapWrapper.mapMode.onNext(
            if(googleMapWrapper.mapMode.value == MAP_MODE.PLACE)
                MAP_MODE.DIRECTION
            else
                MAP_MODE.PLACE
        )
    }

    fun getInfoByLocation(placeId: String) {
        currentPlaceId = placeId

        Log.w(TAG, "current locality $currentLanguage")

        _placeData.value = Result.Loading()

        compositeDisposable.add(
            getInfoByLocationUseCase.invoke(placeId, currentLanguage).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _placeData.value = Result.Success(it)
                }, {
                    _placeData.value = Result.Failure(it)
                })
        )

        /*compositeDisposable.add(
            isPlaceInMarkdownsUseCase.invoke(placeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _currentPlaceFavorite.value = Result.Success(true)
                }, {
                    Log.e(TAG, "place markdowns error ${it.message}")
                    if(it is EmptyResultException)
                        _currentPlaceFavorite.value = Result.Success(false)
                })
        )*/
    }

    fun getDirection() {
        if(googleMapWrapper.origin.value == null || googleMapWrapper.destination.value == null) return

        val origin = googleMapWrapper.origin.value!!.position.toModel()
        val destination = googleMapWrapper.destination.value!!.position.toModel()

        _direction.value = Result.Loading()
        compositeDisposable.add(
            getDirectionUseCase.invoke(origin, destination, directionType, currentLanguage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _direction.value = Result.Success(it)
                }, {
                    _direction.value = Result.Failure(it)
                })
        )
    }

    fun setPlace(place: Place) {
        setPlace(place.id!!, place.latLng!!.toModel())
    }

    /*fun setPlace(markdown: Markdown) {
        if(markdown.location == null) return

        val latLng = LatLng(markdown.location!!.lat, markdown.location!!.lng)
        setPlace(markdown.placeId, latLng.toModel())
    }*/

    private fun setPlace(placeId: String, location: Location) {
        when(googleMapWrapper.mapMode.value) {
            MAP_MODE.PLACE -> {
                googleMapWrapper.createPlaceMarker(location)
                getInfoByLocation(placeId)
            }
            MAP_MODE.DIRECTION -> {
                if(googleMapWrapper.currentDirectionMarker.value == DIRECTION_MARKER.ORIGIN)
                    googleMapWrapper.createOriginMarker(location)
                else
                    googleMapWrapper.createDestinationMarker(location)
            }
        }
        googleMapWrapper.moveCamera(location)
    }

    /*fun toggleFavoriteCurrentPlace() {
        if(currentPlaceId == null)
            return

        _currentPlaceFavorite.value = Result.Loading()

        val subscriber = isPlaceInMarkdownsUseCase.invoke(currentPlaceId!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "update favorite place success")
                val deleteSubscriber = deleteMarkdownByIdUseCase.invoke(currentPlaceId!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _currentPlaceFavorite.value = Result.Success(false)
                    }, {
                        _currentPlaceFavorite.value = Result.Failure(it)
                    })
                compositeDisposable.add(deleteSubscriber)
            }, {
                Log.e(TAG, "update favorite place exception ${it.message}")
                if(it is EmptyResultException) {
                    val placeInfo = (_placeData.value as Result.Success).value
                    val markdown = Markdown(currentPlaceId!!, placeInfo.name, placeInfo.address, placeInfo.location)
                    val insertSubscriber = insertMarkdownUseCase.invoke(markdown)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            _currentPlaceFavorite.value = Result.Success(true)
                        }, {
                            _currentPlaceFavorite.value = Result.Failure(it)
                        })
                    compositeDisposable.add(insertSubscriber)
                }
            })

        compositeDisposable.add(subscriber)
    }*/

    fun getAutocompletePredictions(
        query: String,
        successHandler: (List<AutocompletePrediction>) -> Unit,
        failureHandler: (ApiException) -> Unit
    ) {
        val token = AutocompleteSessionToken.newInstance()
        val request =
            FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener {
            successHandler(it.autocompletePredictions)
        }.addOnFailureListener {
            if (it is ApiException) {
                Log.e(TAG, "Place not found: " + it.statusCode)
                failureHandler(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}