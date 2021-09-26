package com.github.googlemapfragment.android.mappers

import com.github.googlemapfragment.android.api.responses.place_info.*
import com.github.core.models.place_info.PlaceInfo

fun PlaceInfoResponse.toModel(): PlaceInfo {
    return PlaceInfo(
        address = result.formatted_address,
        phoneNumber = result.formatted_phone_number,
        name = result.name,
        openingHours = result.opening_hours?.toModel(),
        photos = result.photos?.map { it.toModel() },
        placeId = result.place_id,
        rating = result.rating,
        reviews = result.reviews?.map { it.toModel() },
        types = result.types,
        url = result.url,
        userRatingsTotal = result.user_ratings_total,
        utcOffset = result.utc_offset,
        website = result.website,
        location = result.geometry?.location?.toModel()
    )
}

fun Photo.toModel(): com.github.core.models.place_info.Photo {
    return com.github.core.models.place_info.Photo(
        height = height,
        htmlAttributions = html_attributions,
        photoReference = photo_reference,
        width = width
    )
}

fun OpeningHours.toModel(): com.github.core.models.place_info.OpeningHours {
    return com.github.core.models.place_info.OpeningHours(
        openNow = open_now,
        periods = periods?.map { it.toModel() },
        weekdayText = weekday_text
    )
}

fun Period.toModel(): com.github.core.models.place_info.Period {
    return com.github.core.models.place_info.Period(
        close = close.toModel(),
        open = open.toModel()
    )
}

fun Time.toModel(): com.github.core.models.place_info.Time {
    return com.github.core.models.place_info.Time(
        day = day,
        time = "${time.substring(0, 2)}:${time.substring(2, 4)}"
    )
}

fun Review.toModel(): com.github.core.models.place_info.Review {
    return com.github.core.models.place_info.Review(
        authorName = author_name,
        authorUrl = author_url,
        profilePhotoUrl = profile_photo_url,
        rating = rating,
        relativeTimeDescription = relative_time_description,
        text = text,
        time = time
    )
}