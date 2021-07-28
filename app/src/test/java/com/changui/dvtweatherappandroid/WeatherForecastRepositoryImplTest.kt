package com.changui.dvtweatherappandroid

import com.changui.dvtweatherappandroid.data.WeatherForecastRepositoryImpl
import com.changui.dvtweatherappandroid.data.local.CurrentWeatherLocalDataStore
import com.changui.dvtweatherappandroid.data.local.CurrentWeatherLocalModel
import com.changui.dvtweatherappandroid.data.local.WeatherForecastLocalDataStore
import com.changui.dvtweatherappandroid.data.local.WeatherForecastLocalModel
import com.changui.dvtweatherappandroid.data.mapper.CurrentWeatherMapper
import com.changui.dvtweatherappandroid.data.mapper.RemoteToLocalWeatherForecastMapper
import com.changui.dvtweatherappandroid.data.remote.*
import com.changui.dvtweatherappandroid.data.remote.commonapimodel.*
import com.changui.dvtweatherappandroid.domain.error.Failure
import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.result.ResultState
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class WeatherForecastRepositoryImplTest {

    @MockK lateinit var currentWeatherRemoteDataStore: CurrentWeatherRemoteDataStore
    @MockK lateinit var currentWeatherMapper: CurrentWeatherMapper
    @MockK lateinit var remoteToLocalWeatherForecastMapper: RemoteToLocalWeatherForecastMapper
    @MockK lateinit var weatherForecastRemoteDataStore: WeatherForecastRemoteDataStore
    @MockK lateinit var weatherForecastLocalDataStore: WeatherForecastLocalDataStore
    @MockK lateinit var currentWeatherLocalDataStore: CurrentWeatherLocalDataStore
    private lateinit var weatherForecastRepositoryImpl: WeatherForecastRepositoryImpl


    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        weatherForecastRepositoryImpl = WeatherForecastRepositoryImpl(
            currentWeatherRemoteDataStore,
            currentWeatherMapper,
            remoteToLocalWeatherForecastMapper,
            weatherForecastRemoteDataStore,
            weatherForecastLocalDataStore,
            currentWeatherLocalDataStore)
    }

    @Nested
    inner class FetchCurrentWeather {
        val params = WeatherPayloadParams(0.0, 0.0, null)
        private val expectedLocalModel = CurrentWeatherLocalModel("cloud", 234.123, 123.56, 45.234)

        @Test
        fun `when fetching current weather repos returns success`() {
            val expectedApiResponse = CurrentWeatherApiResponse(
                "", Clouds(1), 2, Coord(123.45, 45.321), 3, 4, CurrentWeatherTempCondition(0.0, 1, 2, 3.3, 4.4, 5.5),
                "", CurrentWeatherSys("France", 2, 3, 4, 5), 5, 2, emptyList(), Wind(1, 2.44, 5.788)
            )

            coEvery { currentWeatherRemoteDataStore.fetchCurrentWeather(params) } returns ResultState.Success(expectedApiResponse)
            coEvery { currentWeatherMapper.map(expectedApiResponse, null) } returns expectedLocalModel
            coEvery { currentWeatherLocalDataStore.saveCurrentWeather(expectedLocalModel) } returns Unit
            coEvery { currentWeatherLocalDataStore.getCurrentData(params.placeId) } returns expectedLocalModel
            val actualResponse = runBlocking { weatherForecastRepositoryImpl.fetchCurrentWeather(params) }
            actualResponse shouldBeInstanceOf ResultState.Success(CurrentWeatherUIModel::class)::class
        }

        @Test
        fun `when fetching current weather from repository returns failure  due to server error`() {
            val failure = Failure.ServerError
            coEvery { currentWeatherRemoteDataStore.fetchCurrentWeather(params) } returns ResultState.Error(failure, null)
            coEvery { currentWeatherLocalDataStore.getCurrentData(params.placeId) } returns expectedLocalModel
            val actualResponse = runBlocking { weatherForecastRepositoryImpl.fetchCurrentWeather(params) }
            actualResponse shouldBeInstanceOf ResultState.Error::class
        }
    }


    @Nested
    inner class FetchForecastWeather {
        val params = WeatherPayloadParams(0.0, 0.0, null)
        private val expectedLocalList: List<WeatherForecastLocalModel> = listOf(
            WeatherForecastLocalModel("24.55","sunny", "sunny day",
                "15.99", 34, 43, "89.32", "123456789")
        )

        @Test
        fun `when fetching forecast weather list from repository returns success`() {
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

            coEvery { weatherForecastRemoteDataStore.fetchWeatherForecast(params) } returns ResultState.Success(expectedApiResponse)
            coEvery { remoteToLocalWeatherForecastMapper.map(expectedApiResponse) } returns expectedLocalList
            coEvery { weatherForecastLocalDataStore.clearAllWeatherForecast() } returns Unit
            coEvery { weatherForecastLocalDataStore.saveWeatherForecasts(expectedLocalList) } returns Unit
            coEvery { weatherForecastLocalDataStore.getWeatherForecastList() } returns expectedLocalList

            val actualResponse = runBlocking { weatherForecastRepositoryImpl.fetchWeatherForecastList(params) }
            actualResponse.shouldBeInstanceOf<ResultState.Success<MutableList<WeatherForecastUIModelListItem>>>()
        }

        @Test
        fun `when fetching forecast weather repository returns failure due to network error`() {
            val failure = Failure.NetworkError
            coEvery { weatherForecastRemoteDataStore.fetchWeatherForecast(params) } returns ResultState.Error(failure, null)
            coEvery { weatherForecastLocalDataStore.getWeatherForecastList() } returns expectedLocalList

            val actualResponse = runBlocking { weatherForecastRepositoryImpl.fetchWeatherForecastList(params) }
            actualResponse shouldBeInstanceOf ResultState.Error::class
        }

    }


}