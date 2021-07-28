package com.changui.dvtweatherappandroid.data.remote

import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.result.ResultState
import javax.inject.Inject

interface CurrentWeatherRemoteDataStore {
    suspend fun fetchCurrentWeather(params: WeatherPayloadParams): ResultState<CurrentWeatherApiResponse>
}

class CurrentWeatherRemoteDataStoreImpl @Inject constructor (
    private val apiService: WeatherApiService,
    private val errorFactory: CurrentWeatherFailureFactory
) : CurrentWeatherRemoteDataStore {

    override suspend fun fetchCurrentWeather(params: WeatherPayloadParams): ResultState<CurrentWeatherApiResponse> {
        return try {
            val apiResponse = apiService.getWeatherAtLocation(
                params.latitude.toString(),
                params.longitude.toString())
            ResultState.Success(apiResponse)
        } catch (e: Exception) {
            ResultState.Error(errorFactory.produce(e), null)
        }
    }
}