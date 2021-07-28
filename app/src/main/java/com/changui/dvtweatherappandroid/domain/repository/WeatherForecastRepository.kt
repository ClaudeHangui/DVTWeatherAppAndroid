package com.changui.dvtweatherappandroid.domain.repository

import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.result.ResultState

interface WeatherForecastRepository {
    suspend fun fetchCurrentWeather(params: WeatherPayloadParams): ResultState<CurrentWeatherUIModel>
    suspend fun fetchWeatherForecastList(params: WeatherPayloadParams): ResultState<MutableList<WeatherForecastUIModelListItem>>
}