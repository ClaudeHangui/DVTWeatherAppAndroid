package com.changui.dvtweatherappandroid.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkInt

@Entity(tableName = "WeatherLocationBookmark")
data class WeatherLocationBookmarkLocalModel(
    override val place_id: String,
    override val location_name: String,
    override val location_address: String,
    override val latitude: Double,
    override val longitude: Double,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : WeatherLocationBookmarkInt