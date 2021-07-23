package com.changui.dvtweatherappandroid.domain.repository

import arrow.core.Either
import com.changui.dvtweatherappandroid.domain.error.FailureWithCache
import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams

interface WeatherForecastRepository {
    suspend fun fetchCurrentWeather(params: WeatherPayloadParams): Either<FailureWithCache<CurrentWeatherUIModel>, CurrentWeatherUIModel>
    suspend fun fetchWeatherForecastList(params: WeatherPayloadParams): Either<FailureWithCache<MutableList<WeatherForecastUIModelListItem>>, MutableList<WeatherForecastUIModelListItem>>
}