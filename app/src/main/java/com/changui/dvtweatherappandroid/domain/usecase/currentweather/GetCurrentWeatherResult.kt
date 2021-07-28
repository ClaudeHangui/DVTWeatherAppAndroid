package com.changui.dvtweatherappandroid.domain.usecase.currentweather

import com.changui.dvtweatherappandroid.domain.error.FailureWithCache
import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.result.ResultState

sealed class GetCurrentWeatherResult  {
    // object Progress: GetCurrentWeatherResult()
    data class GetCurrentWeatherSuccess(val currentWeather: CurrentWeatherUIModel) : GetCurrentWeatherResult()
    data class GetCurrentWeatherFailure(val error: FailureWithCache<CurrentWeatherUIModel>) : GetCurrentWeatherResult()
}