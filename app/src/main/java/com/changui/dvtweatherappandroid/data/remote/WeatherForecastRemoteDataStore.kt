package com.changui.dvtweatherappandroid.data.remote

import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.result.ResultState
import javax.inject.Inject

interface WeatherForecastRemoteDataStore {
    suspend fun fetchWeatherForecast(params: WeatherPayloadParams): ResultState<FiveDayWeatherForecastApiResponse>
}

class WeatherForecastRemoteDataStoreImpl @Inject constructor (
    private val apiService: WeatherApiService,
    private val errorFactory: WeatherForecastFailureFactory
) : WeatherForecastRemoteDataStore {
    override suspend fun fetchWeatherForecast(params: WeatherPayloadParams): ResultState<FiveDayWeatherForecastApiResponse> {
        return try {
            val apiResponse = apiService.getFiveDayWeatherForecast(
                params.latitude.toString(),
                params.longitude.toString()
            )
            ResultState.Success(apiResponse)
        } catch (e: Exception) {
            ResultState.Error(errorFactory.produce(e), null)
        }
    }
}