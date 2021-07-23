package com.changui.dvtweatherappandroid.data.remote

import com.changui.dvtweatherappandroid.data.remote.commonapimodel.*

data class FiveDayWeatherForecastApiResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<DayForecast>,
    val message: Int
)

data class DayForecast(
    val clouds: Clouds,
    val dt: Int,
    val dt_txt: String,
    val main: ForecastWeatherTempCondition,
    val pop: Float,
    val sys: ForecastWeatherSys,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)

data class ForecastWeatherTempCondition(
    override val feels_like: Double,
    val grnd_level: Int,
    override val humidity: Int,
    override val pressure: Int,
    val sea_level: Int,
    override val temp: Double,
    val temp_kf: Double,
    override val temp_max: Double,
    override val temp_min: Double
) : MainTemperatureCondition

data class ForecastWeatherSys(
    val pod: String
)