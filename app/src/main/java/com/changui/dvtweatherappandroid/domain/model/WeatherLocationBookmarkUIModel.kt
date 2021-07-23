package com.changui.dvtweatherappandroid.domain.model

data class WeatherLocationBookmarkUIModel(
    override val place_id: String,
    override val location_name: String,
    override val location_address: String,
    override val latitude: Double,
    override val longitude: Double
) : WeatherLocationBookmarkInt