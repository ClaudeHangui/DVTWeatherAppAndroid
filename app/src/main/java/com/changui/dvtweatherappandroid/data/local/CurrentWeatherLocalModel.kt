package com.changui.dvtweatherappandroid.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherDataInt

@Entity(tableName = "CurrentWeather")
data class CurrentWeatherLocalModel(
    override val weather_type: String,
    override val min_temp: Double,
    override val current_temp: Double,
    override val max_temp: Double,
    var place_id: String ? = "",
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : CurrentWeatherDataInt