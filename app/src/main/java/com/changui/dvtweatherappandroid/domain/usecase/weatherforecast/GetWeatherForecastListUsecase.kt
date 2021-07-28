package com.changui.dvtweatherappandroid.domain.usecase.weatherforecast

import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.repository.WeatherForecastRepository
import com.changui.dvtweatherappandroid.domain.result.ResultState
import javax.inject.Inject

interface GetWeatherForecastListUsecase  {
    suspend fun invoke(params: WeatherPayloadParams) : ResultState<MutableList<WeatherForecastUIModelListItem>>
}

class GetWeatherForecastListUsecaseImpl @Inject constructor (
    private val repository: WeatherForecastRepository
) : GetWeatherForecastListUsecase {

    override suspend fun invoke(params: WeatherPayloadParams): ResultState<MutableList<WeatherForecastUIModelListItem>> {
        return repository.fetchWeatherForecastList(params)
    }
}