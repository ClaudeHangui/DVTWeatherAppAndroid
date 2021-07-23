package com.changui.dvtweatherappandroid.domain.usecase

import arrow.core.Either
import com.changui.dvtweatherappandroid.domain.error.Failure
import com.changui.dvtweatherappandroid.domain.error.FailureWithCache
import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.model.WeatherPayloadParams
import com.changui.dvtweatherappandroid.domain.repository.WeatherForecastRepository
import com.changui.dvtweatherappandroid.domain.usecase.currentweather.GetCurrentWeatherResult
import com.changui.dvtweatherappandroid.domain.usecase.currentweather.GetCurrentWeatherUseCaseImpl
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
internal class GetCurrentWeatherUseCaseImplTest {
    private lateinit var repository: WeatherForecastRepository
    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCaseImpl
    private val params = WeatherPayloadParams(37.4219983, -122.084)

    @BeforeEach
    fun setUp() {
        repository = mockk()
        getCurrentWeatherUseCase = GetCurrentWeatherUseCaseImpl(repository)
    }

    @Test
    fun `should return GetCurrentWeatherSuccess when repository sends Either type of value Right`() {
        val expectedSuccessResponse = CurrentWeatherUIModel("cloud", 283.95, 286.61, 288.28)
        coEvery { repository.fetchCurrentWeather(params) } returns Either.Right(
            expectedSuccessResponse
        )

        val actualCurrentWeather = runBlocking {
            getCurrentWeatherUseCase.execute(params)
        }
        actualCurrentWeather shouldBeInstanceOf GetCurrentWeatherResult.GetCurrentWeatherSuccess::class
        actualCurrentWeather `should equal` GetCurrentWeatherResult.GetCurrentWeatherSuccess(
            expectedSuccessResponse
        )
    }

    @Nested
    inner class `Failure Scenario` {
        lateinit var remoteFailure: Failure
        val expectedFailureResponse = CurrentWeatherUIModel("cloud", 283.95, 286.61, 288.28)

        @Test
        fun `should return server error if request failed`() {
            remoteFailure = Failure.ServerError
            val failureWithCache = FailureWithCache(remoteFailure, expectedFailureResponse)
            coEvery { repository.fetchCurrentWeather(params) } returns Either.Left(failureWithCache)
            val failureResponse = runBlocking { getCurrentWeatherUseCase.execute(params) }
            failureResponse shouldBeInstanceOf GetCurrentWeatherResult.GetCurrentWeatherFailure::class
        }

        @Test
        fun `should return network error if request failed`() {
            remoteFailure = Failure.NetworkError
            val failureWithCache = FailureWithCache(remoteFailure, expectedFailureResponse)
            coEvery { repository.fetchCurrentWeather(params) } returns Either.Left(failureWithCache)
            val failureResponse = runBlocking { getCurrentWeatherUseCase.execute(params) }

            failureResponse shouldBeInstanceOf GetCurrentWeatherResult.GetCurrentWeatherFailure::class
            failureResponse `should equal` GetCurrentWeatherResult.GetCurrentWeatherFailure(failureWithCache)
        }
    }
}