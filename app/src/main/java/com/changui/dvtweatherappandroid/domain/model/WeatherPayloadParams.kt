package com.changui.dvtweatherappandroid.domain.model

data class WeatherPayloadParams(
    var latitude: Double,
    var longitude: Double,
    var placeId: String ? = ""
)