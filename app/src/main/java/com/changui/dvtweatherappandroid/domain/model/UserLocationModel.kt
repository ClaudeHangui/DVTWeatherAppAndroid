package com.changui.dvtweatherappandroid.domain.model

data class UserLocationModel(
    var longitude: Double,
    var latitude: Double,
    var placeId: String ? = ""
)