package com.changui.dvtweatherappandroid.domain.usecase

import arrow.core.Either
import com.changui.dvtweatherappandroid.domain.error.Failure
import com.changui.dvtweatherappandroid.domain.error.FailureWithCache
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.repository.WeatherForecastRepository
import com.changui.dvtweatherappandroid.domain.usecase.weatherforecast.GetWeatherForecastListResult
import com.changui.dvtweatherappandroid.domain.usecase.weatherforecast.GetWeatherForecastListUsecaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should equal`
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

        coEvery { repository.fetchWeatherForecastList(params) } returns Either.Right(
            expectedSuccessResponse
        )

        val actualWeatherForecasts = runBlocking {
            getWeatherForecastUsecase.execute(params)
        }

        actualWeatherForecasts shouldBeInstanceOf GetWeatherForecastListResult.GetForecastWeatherSuccess::class
        actualWeatherForecasts `should equal` GetWeatherForecastListResult.GetForecastWeatherSuccess(
            expectedSuccessResponse
        )
    }

    @Nested
    inner class `Failure Scenario` {
        lateinit var remoteFailure: Failure
        val cacheItems = mutableListOf<WeatherForecastUIModelListItem>()

        @Test
        fun `should return bad request error if request failed`() {
            remoteFailure = Failure.BadRequestError
            val expectedFailure = FailureWithCache(remoteFailure, cacheItems)
            coEvery { repository.fetchWeatherForecastList(params) } returns Either.Left(expectedFailure)
            val actualFailure = runBlocking { getWeatherForecastUsecase.execute(params) }
            actualFailure shouldBeInstanceOf GetWeatherForecastListResult.GetForecastWeatherFailure::class
        }

        @Test
        fun `should return gateway error if request failed`() {
            remoteFailure = Failure.GatewayError
            val expectedFailure = FailureWithCache(remoteFailure, cacheItems)

            coEvery { repository.fetchWeatherForecastList(params) } returns Either.Left(expectedFailure)
            val actualFailure = runBlocking { getWeatherForecastUsecase.execute(params) }

            actualFailure shouldBeInstanceOf GetWeatherForecastListResult.GetForecastWeatherFailure::class
            actualFailure `should equal` GetWeatherForecastListResult.GetForecastWeatherFailure(
                expectedFailure
            )
        }
    }
}