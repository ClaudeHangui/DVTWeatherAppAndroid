package com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather

import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.domain.result.ResultState

sealed class GetWeatherLocationBookmarksResult : ResultState {
    data class GetWeatherLocationBookmarksSuccess(
        val bookmarks: MutableList<WeatherLocationBookmarkUIModel>
    ) : GetWeatherLocationBookmarksResult()
}