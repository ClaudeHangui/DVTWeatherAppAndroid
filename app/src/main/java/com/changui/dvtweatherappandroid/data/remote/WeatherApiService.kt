package com.changui.dvtweatherappandroid.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

const val OPEN_WEATHER_APP_ID = "f99ec3fc60109a86f36930333a03b382"

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeatherAtLocation(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") appId: String = OPEN_WEATHER_APP_ID
    ): CurrentWeatherApiResponse

    @GET("forecast")
    suspend fun getFiveDayWeatherForecast(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") appId: String = OPEN_WEATHER_APP_ID
    ): FiveDayWeatherForecastApiResponse
}