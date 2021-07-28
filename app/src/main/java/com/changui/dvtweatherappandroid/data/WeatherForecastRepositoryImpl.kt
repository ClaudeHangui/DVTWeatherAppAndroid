package com.changui.dvtweatherappandroid.data

import com.changui.dvtweatherappandroid.data.local.CurrentWeatherLocalDataStore
import com.changui.dvtweatherappandroid.data.local.CurrentWeatherLocalModel
import com.changui.dvtweatherappandroid.data.local.WeatherForecastLocalDataStore
import com.changui.dvtweatherappandroid.data.local.WeatherForecastLocalModel
import com.changui.dvtweatherappandroid.data.mapper.CurrentWeatherMapper
import com.changui.dvtweatherappandroid.data.mapper.RemoteToLocalWeatherForecastMapper
import com.changui.dvtweatherappandroid.data.remote.CurrentWeatherRemoteDataStore
import com.changui.dvtweatherappandroid.data.remote.WeatherForecastRemoteDataStore
import com.changui.dvtweatherappandroid.domain.error.Failure
import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.repository.WeatherForecastRepository
import com.changui.dvtweatherappandroid.domain.result.ResultState
import org.joda.time.LocalDate
import javax.inject.Inject

class WeatherForecastRepositoryImpl @Inject constructor(
    private val currentWeatherRemoteDataStore: CurrentWeatherRemoteDataStore,
    private val mapper: CurrentWeatherMapper,
    private val weatherForecastMapper: RemoteToLocalWeatherForecastMapper,
    private val weatherForecastRemoteDataStore: WeatherForecastRemoteDataStore,
    private val weatherForecastLocalDataStore: WeatherForecastLocalDataStore,
    private val currentWeatherLocalDataStore: CurrentWeatherLocalDataStore,
) : WeatherForecastRepository {

    override suspend fun fetchCurrentWeather(params: WeatherPayloadParams): ResultState<CurrentWeatherUIModel> {
        return when(val result = currentWeatherRemoteDataStore.fetchCurrentWeather(params)) {
            is ResultState.Success -> {
                val mapResult = mapper.map(result.data, params.placeId)
                currentWeatherLocalDataStore.saveCurrentWeather(mapResult)
                val dbCache = currentWeatherLocalDataStore.getCurrentData(params.placeId)
                if (dbCache == null) ResultState.Success(CurrentWeatherUIModel.EMPTY) else ResultState.Success(
                    dbCache.toUIModel()
                )
            }
            is ResultState.Error -> {
                val localCurrentData = currentWeatherLocalDataStore.getCurrentData(params.placeId)
                localCurrentData.returnNullOrData(result.failure)
            }
        }
    }


    override suspend fun fetchWeatherForecastList(params: WeatherPayloadParams): ResultState<MutableList<WeatherForecastUIModelListItem>> {
        return when(val result = weatherForecastRemoteDataStore.fetchWeatherForecast(params)) {
            is ResultState.Success -> {
                val mapResult = weatherForecastMapper.map(result.data)
                weatherForecastLocalDataStore.clearAllWeatherForecast()
                weatherForecastLocalDataStore.saveWeatherForecasts(mapResult)
                val dbData = weatherForecastLocalDataStore.getWeatherForecastList()
                if (dbData.isNullOrEmpty()) ResultState.Success(mutableListOf())
                else ResultState.Success(dbData.toUIModelList())
            }
            is ResultState.Error -> {
                val localCache = weatherForecastLocalDataStore.getWeatherForecastList()
                localCache.returnEmptyFailureOrData(result.failure)
            }
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

    private fun CurrentWeatherLocalModel?.returnNullOrData(failure: Failure): ResultState<CurrentWeatherUIModel> {
        return if (this == null) {
            ResultState.Error(failure, CurrentWeatherUIModel.EMPTY)
        } else ResultState.Error(failure, this.toUIModel())
    }

    private fun List<WeatherForecastLocalModel>?.returnEmptyFailureOrData(failure: Failure): ResultState<MutableList<WeatherForecastUIModelListItem>> {
        return if (this.isNullOrEmpty()) {
            ResultState.Error(failure, mutableListOf())
        } else ResultState.Error(failure, this.toUIModelList())
    }
}