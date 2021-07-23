package com.changui.dvtweatherappandroid.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WeatherForecast")
data class WeatherForecastLocalModel(
    val current_temp: String,
    val weather_type: String,
    val weather_description: String,
    val feeling_temp: String,
    val humidity: Int,
    val pressure: Int,
    val wind_speed: String,
    val day_time: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)