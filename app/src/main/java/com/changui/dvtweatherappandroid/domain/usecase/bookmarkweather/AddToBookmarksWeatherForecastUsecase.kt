package com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather

import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.domain.repository.WeatherLocationRepository
import javax.inject.Inject

interface BookmarkWeatherLocationUsecase {
    suspend fun invoke(params: BookmarkWeatherLocationParams)
    data class BookmarkWeatherLocationParams(val weatherLocation: WeatherLocationBookmarkUIModel)
}

class BookmarkWeatherLocationUsecaseImpl @Inject constructor (
    private val repository: WeatherLocationRepository
) : BookmarkWeatherLocationUsecase {

    override suspend fun invoke(params: BookmarkWeatherLocationUsecase.BookmarkWeatherLocationParams) {
        return repository.saveToBookmarksWeatherLocation(params.weatherLocation)
    }
}