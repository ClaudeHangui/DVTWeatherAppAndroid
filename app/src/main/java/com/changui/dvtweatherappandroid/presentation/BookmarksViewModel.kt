package com.changui.dvtweatherappandroid.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changui.dvtweatherappandroid.domain.model.UserLocationModel
import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchers
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.BookmarkWeatherLocationUsecase
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.GetWeatherLocationBookmarksResult
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.GetWeatherLocationBookmarksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    private val dispatchers: CoroutineDispatchers,
    private val bookmarkWeatherLocationUsecase: BookmarkWeatherLocationUsecase,
    private val getWeatherLocationBookmarksUseCase: GetWeatherLocationBookmarksUseCase
) : ViewModel() {

    private val _latestLocation = LocationLiveData(appContext)
    fun getLocationData(): LiveData<UserLocationModel> {
        return _latestLocation
    }

    private val loadingMutableLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    fun getLoadingLiveData(): LiveData<Boolean> = loadingMutableLiveData

    private val weatherLocationBookmarkListMutableLiveData: MutableLiveData<MutableList<WeatherLocationBookmarkUIModel>> = MutableLiveData<MutableList<WeatherLocationBookmarkUIModel>>()
    fun getWeatherLocationBookmarkListLiveData(): LiveData<MutableList<WeatherLocationBookmarkUIModel>> = weatherLocationBookmarkListMutableLiveData

    fun saveWeatherLocation(
        placeId: String,
        placeName: String,
        placeAddress: String,
        placeLatitude: Double,
        placeLongitude: Double
    ) {
        viewModelScope.launch(dispatchers.main) {
            withContext(dispatchers.io) {
                bookmarkWeatherLocationUsecase.invoke(
                    WeatherLocationBookmarkUIModel(
                        placeId,
                        placeName,
                        placeAddress,
                        placeLatitude,
                        placeLongitude
                    )
                )
            }
        }
    }

    fun getWeatherLocationBookmarks() {
        // loadingMutableLiveData.value = true
        viewModelScope.launch(dispatchers.main) {
            val result = withContext(dispatchers.io) { getWeatherLocationBookmarksUseCase.invoke() } as GetWeatherLocationBookmarksResult.GetWeatherLocationBookmarksSuccess
            weatherLocationBookmarkListMutableLiveData.value = result.bookmarks
            loadingMutableLiveData.value = false
        }
    }
}