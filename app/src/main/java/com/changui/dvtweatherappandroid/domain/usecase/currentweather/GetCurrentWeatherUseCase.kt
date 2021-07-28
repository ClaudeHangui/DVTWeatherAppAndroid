package com.changui.dvtweatherappandroid.domain.usecase.currentweather

import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.repository.WeatherForecastRepository
import com.changui.dvtweatherappandroid.domain.result.ResultState
import javax.inject.Inject

interface GetCurrentWeatherUseCase {
    suspend fun invoke(params: WeatherPayloadParams) : ResultState<CurrentWeatherUIModel>
}

class GetCurrentWeatherUseCaseImpl @Inject constructor(
    private val repository: WeatherForecastRepository
) :
    GetCurrentWeatherUseCase {

    override suspend fun invoke(params: WeatherPayloadParams): ResultState<CurrentWeatherUIModel> {
        return repository.fetchCurrentWeather(params)
    }
}