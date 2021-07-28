package com.changui.dvtweatherappandroid.domain.usecase.weatherforecast

import com.changui.dvtweatherappandroid.domain.error.FailureWithCache
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.result.ResultState

sealed class GetWeatherForecastListResult {
    data class GetForecastWeatherSuccess(val forecasts: MutableList<WeatherForecastUIModelListItem>) : GetWeatherForecastListResult()
    data class GetForecastWeatherFailure(val failure: FailureWithCache<MutableList<WeatherForecastUIModelListItem>>) : GetWeatherForecastListResult()
}