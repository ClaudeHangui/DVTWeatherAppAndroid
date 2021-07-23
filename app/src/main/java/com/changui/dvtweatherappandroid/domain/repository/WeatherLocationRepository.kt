package com.changui.dvtweatherappandroid.domain.repository

import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel

interface WeatherLocationRepository {
    suspend fun saveToBookmarksWeatherLocation(params: WeatherLocationBookmarkUIModel)
    suspend fun getWeatherLocationBookmarks(): MutableList<WeatherLocationBookmarkUIModel>
}