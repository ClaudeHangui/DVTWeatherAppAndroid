package com.changui.dvtweatherappandroid.data.remote

import arrow.core.Either
import com.changui.dvtweatherappandroid.domain.error.Failure
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import java.lang.Exception
import javax.inject.Inject

interface CurrentWeatherRemoteDataStore {
    suspend fun fetchCurrentWeather(params: WeatherPayloadParams): Either<Failure, CurrentWeatherApiResponse>
}

class CurrentWeatherRemoteDataStoreImpl @Inject constructor (
    private val apiService: WeatherApiService,
    private val errorFactory: CurrentWeatherFailureFactory
) : CurrentWeatherRemoteDataStore {

    override suspend fun fetchCurrentWeather(params: WeatherPayloadParams): Either<Failure, CurrentWeatherApiResponse> {
        return try {
            val apiResponse = apiService.getWeatherAtLocation(
                params.latitude.toString(),
                params.longitude.toString())
            Either.Right(apiResponse)
        } catch (e: Exception) {
            Either.Left(errorFactory.produce(e))
        }
    }
}