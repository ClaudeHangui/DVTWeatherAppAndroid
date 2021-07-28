package com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather

import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.domain.repository.WeatherLocationRepository
import javax.inject.Inject

interface GetWeatherLocationBookmarksUseCase {
    suspend operator fun invoke(): MutableList<WeatherLocationBookmarkUIModel>
}

class GetWeatherLocationBookmarksUseCaseImpl @Inject constructor (
    private val repository: WeatherLocationRepository
) :
    GetWeatherLocationBookmarksUseCase {

    override suspend fun invoke(): MutableList<WeatherLocationBookmarkUIModel> {
        return repository.getWeatherLocationBookmarks()
    }
}