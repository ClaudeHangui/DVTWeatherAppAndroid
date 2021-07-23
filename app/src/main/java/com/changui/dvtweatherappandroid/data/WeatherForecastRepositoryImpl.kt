package com.changui.dvtweatherappandroid.data

import arrow.core.Either
import com.changui.dvtweatherappandroid.data.local.CurrentWeatherLocalDataStore
import com.changui.dvtweatherappandroid.data.local.CurrentWeatherLocalModel
import com.changui.dvtweatherappandroid.data.local.WeatherForecastLocalDataStore
import com.changui.dvtweatherappandroid.data.local.WeatherForecastLocalModel
import com.changui.dvtweatherappandroid.data.mapper.CurrentWeatherMapper
import com.changui.dvtweatherappandroid.data.mapper.RemoteToLocalWeatherForecastMapper
import com.changui.dvtweatherappandroid.data.remote.CurrentWeatherRemoteDataStore
import com.changui.dvtweatherappandroid.data.remote.WeatherForecastRemoteDataStore
import com.changui.dvtweatherappandroid.domain.error.Failure
import com.changui.dvtweatherappandroid.domain.error.FailureWithCache
import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.repository.WeatherForecastRepository
import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import javax.inject.Inject

class WeatherForecastRepositoryImpl @Inject constructor(
    private val currentWeatherRemoteDataStore: CurrentWeatherRemoteDataStore,
    private val mapper: CurrentWeatherMapper,
    private val weatherForecastMapper: RemoteToLocalWeatherForecastMapper,
    private val weatherForecastRemoteDataStore: WeatherForecastRemoteDataStore,
    private val weatherForecastLocalDataStore: WeatherForecastLocalDataStore,
    private val currentWeatherLocalDataStore: CurrentWeatherLocalDataStore,
    private val coroutineDispatchers: CoroutineDispatchers,
) : WeatherForecastRepository {

    override suspend fun fetchCurrentWeather(params: WeatherPayloadParams): Either<FailureWithCache<CurrentWeatherUIModel>, CurrentWeatherUIModel> {
        return currentWeatherRemoteDataStore.fetchCurrentWeather(params).fold(
                {
                    val localCurrentData = currentWeatherLocalDataStore.getCurrentData(params.placeId)
                    Either.Left(localCurrentData.returnNullOrData(it))
                },
                {
                    val mapResult = mapper.map(it, params.placeId)
                    currentWeatherLocalDataStore.saveCurrentWeather(mapResult)
                    val dbCache = currentWeatherLocalDataStore.getCurrentData(params.placeId)
                    if (dbCache == null) Either.Right(CurrentWeatherUIModel.EMPTY) else Either.Right(
                        dbCache.toUIModel()
                    )
                }
            )
    }

    override suspend fun fetchWeatherForecastList(params: WeatherPayloadParams): Either<FailureWithCache<MutableList<WeatherForecastUIModelListItem>>, MutableList<WeatherForecastUIModelListItem>> {
        return withContext(coroutineDispatchers.io) {
            weatherForecastRemoteDataStore.fetchWeatherForecast(params).fold(
                {
                    val items = weatherForecastLocalDataStore.getWeatherForecastList().returnEmptyFailureOrData(
                        it
                    )
                    Either.Left(items)
                },
                {
                    val mapResult = weatherForecastMapper.map(it)
                    weatherForecastLocalDataStore.clearAllWeatherForecast()
                    weatherForecastLocalDataStore.saveWeatherForecasts(mapResult)
                    val dbData = weatherForecastLocalDataStore.getWeatherForecastList()
                    if (dbData.isNullOrEmpty()) Either.Right(mutableListOf())
                    else Either.Right(dbData.toUIModelList())
                }
            )
        }
    }

    private fun String.toDay(): String {
        return LocalDate.parse(this.substringBefore(" ")).dayOfWeek().asText
    }

    private fun List<WeatherForecastLocalModel>.toUIModelList(): MutableList<WeatherForecastUIModelListItem> {
        return this.map {
            val day = it.day_time.toDay()
            WeatherForecastUIModelListItem(it.current_temp.toDouble(), day, it.id)
        } as MutableList<WeatherForecastUIModelListItem>
    }

    private fun CurrentWeatherLocalModel.toUIModel(): CurrentWeatherUIModel {
        return CurrentWeatherUIModel(
            this.weather_type,
            this.min_temp,
            this.current_temp,
            this.max_temp
        )
    }

    private fun CurrentWeatherLocalModel?.returnNullOrData(failure: Failure): FailureWithCache<CurrentWeatherUIModel> {
        return if (this == null) {
            FailureWithCache(failure, CurrentWeatherUIModel.EMPTY)
        } else FailureWithCache(failure, this.toUIModel())
    }

    private fun List<WeatherForecastLocalModel>?.returnEmptyFailureOrData(failure: Failure): FailureWithCache<MutableList<WeatherForecastUIModelListItem>> {
        return if (this.isNullOrEmpty()) {
            FailureWithCache(failure, mutableListOf())
        } else FailureWithCache(failure, this.toUIModelList())
    }
}