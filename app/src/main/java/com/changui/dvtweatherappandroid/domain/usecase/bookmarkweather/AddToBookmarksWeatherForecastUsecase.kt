package com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather

import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.domain.repository.WeatherLocationRepository
import com.changui.dvtweatherappandroid.domain.usecase.UseCaseWithParams
import javax.inject.Inject

interface BookmarkWeatherLocationUsecase : UseCaseWithParams<BookmarkWeatherLocationResult, WeatherLocationBookmarkUIModel> {
    suspend operator fun invoke(weatherLocation: WeatherLocationBookmarkUIModel) = execute(
        weatherLocation
    )
}

class BookmarkWeatherLocationUsecaseImpl @Inject constructor (
    private val repository: WeatherLocationRepository
) : BookmarkWeatherLocationUsecase {
    override suspend fun execute(params: WeatherLocationBookmarkUIModel): BookmarkWeatherLocationResult {
        repository.saveToBookmarksWeatherLocation(params)
        return BookmarkWeatherLocationResult
    }
}