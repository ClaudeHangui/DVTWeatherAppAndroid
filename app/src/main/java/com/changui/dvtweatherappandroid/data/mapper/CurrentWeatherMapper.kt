package com.changui.dvtweatherappandroid.data.mapper

import com.changui.dvtweatherappandroid.data.local.CurrentWeatherLocalModel
import com.changui.dvtweatherappandroid.data.remote.CurrentWeatherApiResponse
import com.changui.dvtweatherappandroid.data.remote.commonapimodel.Weather
import com.changui.dvtweatherappandroid.domain.mapper.MapperDualInput

class CurrentWeatherMapper : MapperDualInput<CurrentWeatherApiResponse, String, CurrentWeatherLocalModel> {
    override fun map(input1: CurrentWeatherApiResponse, inputI2: String?): CurrentWeatherLocalModel {
        return CurrentWeatherLocalModel(
            input1.weather.first().toWeatherType(),
            input1.main.temp_min,
            input1.main.temp,
            input1.main.temp_max,
            inputI2 ?: ""
        )
    }
}

fun Weather?.toWeatherType(): String {
    return this?.main ?: "Other"
}