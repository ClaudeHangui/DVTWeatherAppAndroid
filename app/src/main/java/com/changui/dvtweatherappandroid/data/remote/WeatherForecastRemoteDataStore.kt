package com.changui.dvtweatherappandroid.data.remote

import arrow.core.Either
import com.changui.dvtweatherappandroid.domain.error.Failure
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import java.lang.Exception
import javax.inject.Inject

interface WeatherForecastRemoteDataStore {
    suspend fun fetchWeatherForecast(params: WeatherPayloadParams): Either<Failure, FiveDayWeatherForecastApiResponse>
}

class WeatherForecastRemoteDataStoreImpl @Inject constructor (
    private val apiService: WeatherApiService,
    private val errorFactory: WeatherForecastFailureFactory
) : WeatherForecastRemoteDataStore {
    override suspend fun fetchWeatherForecast(params: WeatherPayloadParams): Either<Failure, FiveDayWeatherForecastApiResponse> {
        return try {
            val apiResponse = apiService.getFiveDayWeatherForecast(
                params.latitude.toString(),
                params.longitude.toString()
            )
            Either.Right(apiResponse)
        } catch (e: Exception) {
            Either.Left(errorFactory.produce(e))
        }
    }
}