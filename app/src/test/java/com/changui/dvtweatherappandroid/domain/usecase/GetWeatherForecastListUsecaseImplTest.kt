package com.changui.dvtweatherappandroid.domain.usecase

import com.changui.dvtweatherappandroid.domain.error.Failure
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.repository.WeatherForecastRepository
import com.changui.dvtweatherappandroid.domain.result.ResultState
import com.changui.dvtweatherappandroid.domain.usecase.weatherforecast.GetWeatherForecastListUsecaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class GetWeatherForecastListUsecaseImplTest {
    private lateinit var repository: WeatherForecastRepository
    private lateinit var getWeatherForecastUsecase: GetWeatherForecastListUsecaseImpl
    private val params = WeatherPayloadParams(37.4219983, -122.084)

    @BeforeEach
    fun setUp() {
        repository = mockk()
        getWeatherForecastUsecase = GetWeatherForecastListUsecaseImpl(repository)
    }


    @Test
    fun `should return GetWeatherForecastUsecaseSuccess when repository sends Either type of value Right`() {
        val expectedSuccessResponse = mutableListOf(
            WeatherForecastUIModelListItem(283.95, "Monday"),
            WeatherForecastUIModelListItem(285.59, "Tuesday"),
            WeatherForecastUIModelListItem(288.67, "Wednesday")
        )

        coEvery { repository.fetchWeatherForecastList(params) } returns ResultState.Success(
            expectedSuccessResponse
        )

        val actualWeatherForecasts = runBlocking {
            getWeatherForecastUsecase.invoke(params)
        }

        actualWeatherForecasts shouldBeInstanceOf ResultState.Success::class
    }

    @Nested
    inner class `Failure Scenario` {
        lateinit var remoteFailure: Failure
        val cacheItems = mutableListOf<WeatherForecastUIModelListItem>()

        @Test
        fun `should return bad request error if request failed`() {
            remoteFailure = Failure.BadRequestError
            coEvery { repository.fetchWeatherForecastList(params) } returns ResultState.Error(remoteFailure, cacheItems)
            val actualFailure = runBlocking { getWeatherForecastUsecase.invoke(params) }
            actualFailure shouldBeInstanceOf ResultState.Error::class
        }

        @Test
        fun `should return gateway error if request failed`() {
            remoteFailure = Failure.GatewayError

            coEvery { repository.fetchWeatherForecastList(params) } returns ResultState.Error(remoteFailure, cacheItems)
            val actualFailure = runBlocking { getWeatherForecastUsecase.invoke(params) }

            actualFailure shouldBeInstanceOf ResultState.Error::class
        }
    }

}