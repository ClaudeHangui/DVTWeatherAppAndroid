package com.changui.dvtweatherappandroid.data.mapper

import com.changui.dvtweatherappandroid.data.local.WeatherForecastLocalModel
import com.changui.dvtweatherappandroid.data.remote.FiveDayWeatherForecastApiResponse
import com.changui.dvtweatherappandroid.domain.mapper.Mapper

class RemoteToLocalWeatherForecastMapper :
    Mapper<FiveDayWeatherForecastApiResponse, List<WeatherForecastLocalModel>> {
    override fun map(input: FiveDayWeatherForecastApiResponse): List<WeatherForecastLocalModel> {
        return input.list.map {
            WeatherForecastLocalModel(
                it.main.temp.toString(),
                it.weather.firstOrNull()?.main.orEmpty(),
                it.weather.firstOrNull()?.description.orEmpty(),
                it.main.feels_like.toString(),
                it.main.humidity,
                it.main.pressure,
                it.wind.speed.toString(),
                it.dt_txt
            )
        }
    }
}