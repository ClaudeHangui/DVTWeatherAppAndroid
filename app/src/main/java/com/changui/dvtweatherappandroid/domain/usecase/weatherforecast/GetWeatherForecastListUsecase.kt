package com.changui.dvtweatherappandroid.domain.usecase.weatherforecast

import arrow.core.Either
import com.changui.dvtweatherappandroid.domain.error.FailureWithCache
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.repository.WeatherForecastRepository
import com.changui.dvtweatherappandroid.domain.usecase.UseCaseWithParams
import javax.inject.Inject

interface GetWeatherForecastListUsecase : UseCaseWithParams<GetWeatherForecastListResult, WeatherPayloadParams> {
    suspend operator fun invoke(params: WeatherPayloadParams) = execute(params)
}

class GetWeatherForecastListUsecaseImpl @Inject constructor (
    private val repository: WeatherForecastRepository
) : GetWeatherForecastListUsecase {
    override suspend fun execute(params: WeatherPayloadParams): GetWeatherForecastListResult {
        return repository.fetchWeatherForecastList(params).toResult()
    }

    private fun Either<FailureWithCache<MutableList<WeatherForecastUIModelListItem>>, MutableList<WeatherForecastUIModelListItem>>.toResult(): GetWeatherForecastListResult =
        when (this) {
            is Either.Left -> GetWeatherForecastListResult.GetForecastWeatherFailure(this.value)
            is Either.Right -> GetWeatherForecastListResult.GetForecastWeatherSuccess(this.value)
        }
}