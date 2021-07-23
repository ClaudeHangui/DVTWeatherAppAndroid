package com.changui.dvtweatherappandroid.data.remote

import com.changui.dvtweatherappandroid.data.remote.commonapimodel.*

data class CurrentWeatherApiResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: CurrentWeatherTempCondition,
    val name: String,
    val sys: CurrentWeatherSys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)

data class CurrentWeatherTempCondition(
    override val feels_like: Double,
    override val humidity: Int,
    override val pressure: Int,
    override val temp: Double,
    override val temp_max: Double,
    override val temp_min: Double
) : MainTemperatureCondition

data class CurrentWeatherSys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)