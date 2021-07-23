package com.changui.dvtweatherappandroid.data.local

import javax.inject.Inject

interface WeatherForecastLocalDataStore {
    suspend fun getWeatherForecastList(): List<WeatherForecastLocalModel>
    suspend fun clearAllWeatherForecast()
    suspend fun saveWeatherForecasts(items: List<WeatherForecastLocalModel>)
}

class WeatherForecastLocalDataStoreImpl @Inject constructor (
    private val dao: WeatherForecastDao
    ) : WeatherForecastLocalDataStore {

    override suspend fun getWeatherForecastList(): List<WeatherForecastLocalModel> {
        return dao.getWeatherForecastList()
    }

    override suspend fun clearAllWeatherForecast() {
        dao.deleteAll()
    }

    override suspend fun saveWeatherForecasts(items: List<WeatherForecastLocalModel>) {
        dao.insertAll(items)
    }
}