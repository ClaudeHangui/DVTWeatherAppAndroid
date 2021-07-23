package com.changui.dvtweatherappandroid.data.mapper

import com.changui.dvtweatherappandroid.data.remote.DayForecast
import com.changui.dvtweatherappandroid.data.remote.FiveDayWeatherForecastApiResponse
import com.changui.dvtweatherappandroid.data.remote.ForecastWeatherSys
import com.changui.dvtweatherappandroid.data.remote.ForecastWeatherTempCondition
import com.changui.dvtweatherappandroid.data.remote.commonapimodel.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RemoteToLocalWeatherForecastMapperTest {

    private lateinit var remoteToLocalWeatherForecastMapper: RemoteToLocalWeatherForecastMapper

    @BeforeEach
    fun setUp() {
        remoteToLocalWeatherForecastMapper = RemoteToLocalWeatherForecastMapper()
    }

    @Test
    fun `given a non-null weather forecast api response mapper should a list of WeatherForecastLocalModel`() {
        val expectedApiResponse = FiveDayWeatherForecastApiResponse(
            City(Coord(23.23, 45.56), "Paris", 3, "Paris", 2444, 23, 45, 9),
            5,
            "cod",
            listOf(
                DayForecast(
                    Clouds(3),
                    3,
                    "13-1-2021",
                    ForecastWeatherTempCondition(123.4, 5, 6, 8, 9, 56.8, 34.1, 567.5, 324.6),
                    1.4F,
                    ForecastWeatherSys("pod"),
                    1,
                    listOf(Weather("cloudy", "icon", 2, "cloudy day")),
                    Wind(1, 2.34, 55.3)
                )
            ),
            2
        )

        val weatherForecastLocalList = remoteToLocalWeatherForecastMapper.map(expectedApiResponse)
        assert(weatherForecastLocalList.isNotEmpty())
        assert(weatherForecastLocalList.size == 1)
    }
}