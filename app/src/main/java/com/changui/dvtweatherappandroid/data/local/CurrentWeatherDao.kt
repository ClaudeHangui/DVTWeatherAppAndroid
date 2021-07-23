package com.changui.dvtweatherappandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCurrentWeather(currentWeatherLocalModel: CurrentWeatherLocalModel)

    @Query("select * from CurrentWeather")
    fun getCurrentWeather(): CurrentWeatherLocalModel?

    @Query("delete from CurrentWeather")
    fun deleteCurrentWeather()

    @Query("select * from CurrentWeather where place_id = :placeId")
    fun getBookmarkCurrentWeather(placeId: String): CurrentWeatherLocalModel?
}