package com.changui.dvtweatherappandroid.data.local

import javax.inject.Inject

interface CurrentWeatherLocalDataStore {
    suspend fun saveCurrentWeather(currentWeatherData: CurrentWeatherLocalModel)
    suspend fun getCurrentData(placeId: String?): CurrentWeatherLocalModel?
}

class CurrentWeatherLocalDataStoreImpl @Inject constructor (
    private val dao: CurrentWeatherDao
) : CurrentWeatherLocalDataStore {
    override suspend fun saveCurrentWeather(currentWeatherData: CurrentWeatherLocalModel) {
        dao.saveCurrentWeather(currentWeatherData)
    }

    override suspend fun getCurrentData(placeId: String?): CurrentWeatherLocalModel? {
        return if (placeId.isNullOrEmpty()) dao.getCurrentWeather()
        else dao.getBookmarkCurrentWeather(placeId)
    }
}