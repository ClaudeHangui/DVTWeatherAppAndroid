package com.changui.dvtweatherappandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherForecastDao {
    @Query("delete from WeatherForecast")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(weatherForecast: List<WeatherForecastLocalModel>)

    @Query("select * from WeatherForecast")
    fun getWeatherForecastList(): List<WeatherForecastLocalModel>
}