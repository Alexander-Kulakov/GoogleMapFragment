package com.github.googlemapfragment.android.mappers

import com.github.googlemapfragment.android.api.responses.Location
import com.github.googlemapfragment.android.api.responses.directions.*
import com.github.core.models.directions.Direction

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

fun Bounds.toModel(): com.github.core.models.directions.Bounds {
    return com.github.core.models.directions.Bounds(
        northeast = northeast.toModel(),
        southwest = southwest.toModel()
    )
}

fun Route.toModel(): com.github.core.models.directions.Route {
    return com.github.core.models.directions.Route(
        legs = legs.map { it.toModel() },
        overview_polyline = overview_polyline.toModel(),
        summary = summary
    )
}

fun Leg.toModel(): com.github.core.models.directions.Leg {
    return com.github.core.models.directions.Leg(
        distance = distance.toModel(),
        duration = duration.toModel(),
        end_address = end_address,
        end_location = end_location.toModel(),
        start_location = start_location.toModel(),
        start_address = start_address,
        steps = steps?.map { it.toModel() }
    )
}

fun Step.toModel(): com.github.core.models.directions.Step {
    return com.github.core.models.directions.Step(
        distance = distance.toModel(),
        duration = duration.toModel(),
        end_location = end_location.toModel(),
        html_instructions = html_instructions,
        polyline = polyline.toModel(),
        start_location = start_location.toModel(),
        travel_mode = travel_mode
    )
}

fun Distance.toModel(): com.github.core.models.directions.Distance {
    return com.github.core.models.directions.Distance(
        text = text,
        value = value
    )
}

fun Duration.toModel(): com.github.core.models.directions.Duration {
    return com.github.core.models.directions.Duration(
        text = text,
        value = value
    )
}

fun Location.toModel(): com.github.core.models.Location {
    return com.github.core.models.Location(
        lat = lat,
        lng = lng
    )
}

fun Polyline.toModel(): com.github.core.models.directions.Polyline {
    return com.github.core.models.directions.Polyline(
        points = points
    )
}