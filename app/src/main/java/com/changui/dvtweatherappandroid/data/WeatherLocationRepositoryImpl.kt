package com.changui.dvtweatherappandroid.data

import com.changui.dvtweatherappandroid.data.local.WeatherLocationBookmarkLocalModel
import com.changui.dvtweatherappandroid.data.local.WeatherLocationLocalDataStore
import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.domain.repository.WeatherLocationRepository
import javax.inject.Inject

class WeatherLocationRepositoryImpl @Inject constructor (
    private val weatherLocationLocalDataStore: WeatherLocationLocalDataStore)
    : WeatherLocationRepository {
    override suspend fun saveToBookmarksWeatherLocation(params: WeatherLocationBookmarkUIModel) {
        weatherLocationLocalDataStore.saveToWeatherLocationBookmarks(params)
    }

    override suspend fun getWeatherLocationBookmarks(): MutableList<WeatherLocationBookmarkUIModel> {
        val bookmarksLocalModel = weatherLocationLocalDataStore.getWeatherLocationBookmarks()
        return bookmarksLocalModel.toUIModelList()
    }

    private fun List<WeatherLocationBookmarkLocalModel>.toUIModelList(): MutableList<WeatherLocationBookmarkUIModel> {
        return this.map {
            WeatherLocationBookmarkUIModel(
                it.place_id,
                it.location_name,
                it.location_address,
                it.latitude,
                it.longitude
            )
        } as MutableList<WeatherLocationBookmarkUIModel>
    }
}