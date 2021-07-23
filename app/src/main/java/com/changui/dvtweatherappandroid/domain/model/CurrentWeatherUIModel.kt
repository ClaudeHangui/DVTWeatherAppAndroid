package com.changui.dvtweatherappandroid.domain.model

data class CurrentWeatherUIModel(
    override val weather_type: String,
    override val min_temp: Double,
    override val current_temp: Double,
    override val max_temp: Double
) : CurrentWeatherDataInt {
    companion object {
        val EMPTY = CurrentWeatherUIModel("", 0.0, 0.0, 0.0)
    }
}


interface CurrentWeatherDataInt {
    val weather_type: String
    val min_temp: Double
    val current_temp: Double
    val max_temp: Double
}