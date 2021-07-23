package com.changui.dvtweatherappandroid.data

import com.changui.dvtweatherappandroid.data.mapper.CurrentWeatherMapper
import com.changui.dvtweatherappandroid.data.remote.CurrentWeatherApiResponse
import com.changui.dvtweatherappandroid.data.remote.CurrentWeatherSys
import com.changui.dvtweatherappandroid.data.remote.CurrentWeatherTempCondition
import com.changui.dvtweatherappandroid.data.remote.commonapimodel.Clouds
import com.changui.dvtweatherappandroid.data.remote.commonapimodel.Coord
import com.changui.dvtweatherappandroid.data.remote.commonapimodel.Weather
import com.changui.dvtweatherappandroid.data.remote.commonapimodel.Wind
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CurrentWeatherMapperTest {

    private lateinit var currentWeatherMapper: CurrentWeatherMapper

    @BeforeEach
    fun setUp() {
        currentWeatherMapper = CurrentWeatherMapper()
    }

    @Test
    fun `given a non-null api response, mapper returns a CurrentWeatherLocalModel`() {
        val expectedApiResponse = CurrentWeatherApiResponse(
            "", Clouds(1), 2, Coord(123.45, 45.321), 3, 4, CurrentWeatherTempCondition(0.0, 1, 2, 3.3, 4.4, 5.5),
            "", CurrentWeatherSys("France", 2, 3, 4, 5), 5, 2,
            listOf(Weather("cloudy day", "icon", 2, "cloudy")),
             Wind(1, 2.44, 5.788)
        )

        val currentWeatherLocalModel = currentWeatherMapper.map(expectedApiResponse, null)

        assert(currentWeatherLocalModel.current_temp == 3.3)
        currentWeatherLocalModel.weather_type `should be equal to` "cloudy"
    }
}