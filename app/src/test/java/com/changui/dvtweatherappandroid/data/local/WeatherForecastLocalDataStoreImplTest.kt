package com.changui.dvtweatherappandroid.data.local

import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchers
import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchersImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class WeatherForecastLocalDataStoreImplTest {

    @MockK lateinit var dao: WeatherForecastDao
    @MockK private lateinit var dispatchers: CoroutineDispatchers
    private lateinit var weatherForecastLocalDataStoreImpl: WeatherForecastLocalDataStoreImpl

    private val expectedList = listOf(
        WeatherForecastLocalModel("24.55","sunny", "sunny day",
            "15.99", 34, 43, "89.32", "123456789")
    )
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery {
            dispatchers.io
        } returns CoroutineDispatchersImpl().io
        weatherForecastLocalDataStoreImpl = WeatherForecastLocalDataStoreImpl(dao)
    }

    @Test
    fun `after fetching weather forecast from server clean the db`() {
        coEvery { dao.deleteAll() } returns Unit
        runBlocking { weatherForecastLocalDataStoreImpl.clearAllWeatherForecast() }
        coVerify(exactly = 1) { dao.deleteAll() }
    }

    @Test
    fun `after fetching weather forecast from server save new data`() {
        coEvery { dao.insertAll(expectedList) } returns Unit
        runBlocking { weatherForecastLocalDataStoreImpl.saveWeatherForecasts(expectedList) }
        coVerify(exactly = 1) { dao.insertAll(expectedList) }
    }

    @Test
    fun `calling getWeatherForecastList() method return list of weather forecast`() {
        coEvery { dao.getWeatherForecastList() } returns expectedList

        val actualList = runBlocking { weatherForecastLocalDataStoreImpl.getWeatherForecastList() }
        coVerify(exactly = 1) { dao.getWeatherForecastList() }

        actualList.size `should be equal to` 1
    }

}