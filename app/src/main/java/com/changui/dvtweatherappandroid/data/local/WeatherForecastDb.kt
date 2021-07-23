package com.changui.dvtweatherappandroid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WeatherForecastLocalModel::class, WeatherLocationBookmarkLocalModel::class, CurrentWeatherLocalModel::class],
    version = 2
)
abstract class WeatherForecastDb : RoomDatabase() {
    abstract fun weatherForecastDao(): WeatherForecastDao
    abstract fun weatherLocationDao(): WeatherLocationDao
    abstract fun currentWeatherDao(): CurrentWeatherDao
}