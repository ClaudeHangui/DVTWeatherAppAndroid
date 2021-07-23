package com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather

import com.changui.dvtweatherappandroid.domain.repository.WeatherLocationRepository
import com.changui.dvtweatherappandroid.domain.usecase.UseCaseWithoutParam
import javax.inject.Inject

interface GetWeatherLocationBookmarksUseCase : UseCaseWithoutParam<GetWeatherLocationBookmarksResult> {
    suspend operator fun invoke() = execute()
}

class GetWeatherLocationBookmarksUseCaseImpl @Inject constructor (
    private val repository: WeatherLocationRepository
) :
    GetWeatherLocationBookmarksUseCase {
    override suspend fun execute(): GetWeatherLocationBookmarksResult {
        return GetWeatherLocationBookmarksResult.GetWeatherLocationBookmarksSuccess(
            repository.getWeatherLocationBookmarks()
        )
    }
}