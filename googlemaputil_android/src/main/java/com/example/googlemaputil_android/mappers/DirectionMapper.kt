package com.example.googlemaputil_android.mappers

import com.example.googlemaputil_android.api.responses.Location
import com.example.googlemaputil_android.api.responses.directions.*
import com.example.googlemaputil_core.models.directions.Direction

fun DirectionResponse.toModel(): Direction {
    val leg = routes?.firstOrNull()?.legs?.firstOrNull()

    return Direction(
        bounds = bounds?.toModel(),
        routes = routes?.map { it.toModel() },
        total_distance = leg?.distance?.toModel(),
        total_duration = leg?.duration?.toModel(),
        status = status
    )
}

fun Bounds.toModel(): com.example.googlemaputil_core.models.directions.Bounds {
    return com.example.googlemaputil_core.models.directions.Bounds(
        northeast = northeast.toModel(),
        southwest = southwest.toModel()
    )
}

fun Route.toModel(): com.example.googlemaputil_core.models.directions.Route {
    return com.example.googlemaputil_core.models.directions.Route(
        legs = legs.map { it.toModel() },
        overview_polyline = overview_polyline.toModel(),
        summary = summary
    )
}

fun Leg.toModel(): com.example.googlemaputil_core.models.directions.Leg {
    return com.example.googlemaputil_core.models.directions.Leg(
        distance = distance.toModel(),
        duration = duration.toModel(),
        end_address = end_address,
        end_location = end_location.toModel(),
        start_location = start_location.toModel(),
        start_address = start_address,
        steps = steps?.map { it.toModel() }
    )
}

fun Step.toModel(): com.example.googlemaputil_core.models.directions.Step {
    return com.example.googlemaputil_core.models.directions.Step(
        distance = distance.toModel(),
        duration = duration.toModel(),
        end_location = end_location.toModel(),
        html_instructions = html_instructions,
        polyline = polyline.toModel(),
        start_location = start_location.toModel(),
        travel_mode = travel_mode
    )
}

fun Distance.toModel(): com.example.googlemaputil_core.models.directions.Distance {
    return com.example.googlemaputil_core.models.directions.Distance(
        text = text,
        value = value
    )
}

fun Duration.toModel(): com.example.googlemaputil_core.models.directions.Duration {
    return com.example.googlemaputil_core.models.directions.Duration(
        text = text,
        value = value
    )
}

fun Location.toModel(): com.example.googlemaputil_core.models.Location {
    return com.example.googlemaputil_core.models.Location(
        lat = lat,
        lng = lng
    )
}

fun Polyline.toModel(): com.example.googlemaputil_core.models.directions.Polyline {
    return com.example.googlemaputil_core.models.directions.Polyline(
        points = points
    )
}