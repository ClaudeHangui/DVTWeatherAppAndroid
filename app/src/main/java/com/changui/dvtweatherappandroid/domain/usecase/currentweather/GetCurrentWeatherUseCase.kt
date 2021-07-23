package com.changui.dvtweatherappandroid.domain.usecase.currentweather

import arrow.core.Either
import com.changui.dvtweatherappandroid.domain.error.FailureWithCache
import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.repository.WeatherForecastRepository
import com.changui.dvtweatherappandroid.domain.usecase.UseCaseWithParams
import javax.inject.Inject

interface GetCurrentWeatherUseCase :
    UseCaseWithParams<GetCurrentWeatherResult, WeatherPayloadParams> {
    suspend operator fun invoke(params: WeatherPayloadParams) = execute(params)
}

class GetCurrentWeatherUseCaseImpl @Inject constructor(
    private val repository: WeatherForecastRepository
) :
    GetCurrentWeatherUseCase {
    override suspend fun execute(params: WeatherPayloadParams): GetCurrentWeatherResult {
        return repository.fetchCurrentWeather(params).toResult()
    }

    private fun Either<FailureWithCache<CurrentWeatherUIModel>, CurrentWeatherUIModel>.toResult(): GetCurrentWeatherResult =
        when (this) {
            is Either.Left -> GetCurrentWeatherResult.GetCurrentWeatherFailure(this.value)
            is Either.Right -> GetCurrentWeatherResult.GetCurrentWeatherSuccess(this.value)
        }
}