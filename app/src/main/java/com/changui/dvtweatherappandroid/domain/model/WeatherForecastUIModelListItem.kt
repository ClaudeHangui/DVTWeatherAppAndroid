package com.changui.dvtweatherappandroid.domain.model

data class WeatherForecastUIModelListItem(
    val current_temp: Double,
    val day_of_week: String,
    var weather_type_separator: Int = -1,
    val id: Int? = -1,
)