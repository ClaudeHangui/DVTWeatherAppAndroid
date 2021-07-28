package com.changui.dvtweatherappandroid.presentation

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.model.UserLocationModel
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.result.ResultState
import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchers
import com.changui.dvtweatherappandroid.domain.usecase.currentweather.GetCurrentWeatherUseCase
import com.changui.dvtweatherappandroid.domain.usecase.weatherforecast.GetWeatherForecastListUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    private val dispatchers: CoroutineDispatchers,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getWeatherForecastListUsecase: GetWeatherForecastListUsecase
) : ViewModel() {

    private val _latestLocation = LocationLiveData(appContext)
    fun getLocationData(): LiveData<UserLocationModel> {
        return _latestLocation
    }

    private val _activeNetworkInfoLiveData: MutableLiveData<Boolean> =
        InternetAccessLiveData(appContext, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) {
            context: Context, _: Intent? ->
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            (cm).activeNetworkInfo?.isConnected == true
        }

    fun getInternetAccessLiveData(): LiveData<Boolean> = _activeNetworkInfoLiveData

    private val loadingMutableLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    fun getLoadingLiveData(): LiveData<Boolean> = loadingMutableLiveData


    private val currentWeatherMutableLiveData: MutableLiveData<ResultState<CurrentWeatherUIModel>> = MutableLiveData<ResultState<CurrentWeatherUIModel>>()
    fun getCurrentWeatherLiveData(): LiveData<ResultState<CurrentWeatherUIModel>> = currentWeatherMutableLiveData

    private val forecastWeatherListMutableLiveData: MutableLiveData<ResultState<MutableList<WeatherForecastUIModelListItem>>> = MutableLiveData<ResultState<MutableList<WeatherForecastUIModelListItem>>>()
    fun getForecastWeatherListLiveData(): LiveData<ResultState<MutableList<WeatherForecastUIModelListItem>>> = forecastWeatherListMutableLiveData


    fun getCurrentWeather(locationModel: UserLocationModel) {
        viewModelScope.launch(dispatchers.main) {
            val response = withContext(dispatchers.io) {
                getCurrentWeatherUseCase.invoke(
                    WeatherPayloadParams(
                        locationModel.latitude,
                        locationModel.longitude,
                        locationModel.placeId
                    )
                )
            }
            currentWeatherMutableLiveData.value = response
        }
    }

    fun getForecastWeather(locationModel: UserLocationModel) {
        loadingMutableLiveData.value = true
        viewModelScope.launch(dispatchers.main) {
            val result = withContext(dispatchers.io) {
                getWeatherForecastListUsecase.invoke(
                    WeatherPayloadParams(locationModel.latitude, locationModel.longitude)
                )
            }
            forecastWeatherListMutableLiveData.value = result
        }
    }
}