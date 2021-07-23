package com.changui.dvtweatherappandroid.data.local

import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import javax.inject.Inject

interface WeatherLocationLocalDataStore {
    suspend fun saveToWeatherLocationBookmarks(weatherLocation: WeatherLocationBookmarkUIModel)
    suspend fun getWeatherLocationBookmarks(): List<WeatherLocationBookmarkLocalModel>
}

class WeatherLocationLocalDataStoreImpl @Inject constructor(private val dao: WeatherLocationDao)
    : WeatherLocationLocalDataStore {
    override suspend fun saveToWeatherLocationBookmarks(
        weatherLocation: WeatherLocationBookmarkUIModel
    ) {
            dao.saveToBookmarks(weatherLocation.toLocalModel())
    }

    override suspend fun getWeatherLocationBookmarks(): List<WeatherLocationBookmarkLocalModel> {
        return dao.getAllWeatherLocationBookmarks()
    }

    private fun WeatherLocationBookmarkUIModel.toLocalModel(): WeatherLocationBookmarkLocalModel {
        return WeatherLocationBookmarkLocalModel(
            this.place_id,
            this.location_name,
            this.location_address,
            this.latitude,
            this.longitude
        )
    }
}