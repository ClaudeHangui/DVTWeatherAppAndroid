package com.changui.dvtweatherappandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveToBookmarks(weatherId: WeatherLocationBookmarkLocalModel)

    @Query("select * from WeatherLocationBookmark")
    fun getAllWeatherLocationBookmarks(): List<WeatherLocationBookmarkLocalModel>
}