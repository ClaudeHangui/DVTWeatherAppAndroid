package com.changui.dvtweatherappandroid.data.local

import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchers
import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchersImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CurrentWeatherLocalDataStoreImplTest {

    @MockK lateinit var dao: CurrentWeatherDao
    @MockK private lateinit var dispatchers: CoroutineDispatchers
    private lateinit var currentWeatherLocalDataStore: CurrentWeatherLocalDataStoreImpl
    private val expectedLocalModel = CurrentWeatherLocalModel("cloud", 234.123, 123.56, 45.234)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery {
            dispatchers.io
        } returns CoroutineDispatchersImpl().io
        currentWeatherLocalDataStore = CurrentWeatherLocalDataStoreImpl(dao)
    }

    @Test
    fun `get the current weather of the user from the local data store`() {
        coEvery { dao.getCurrentWeather() } returns expectedLocalModel
        val actualLocalModel = runBlocking { currentWeatherLocalDataStore.getCurrentData("")!! }
        coVerify(exactly = 1) { dao.getCurrentWeather() }

        actualLocalModel shouldBeInstanceOf CurrentWeatherLocalModel::class
        actualLocalModel.weather_type `should be equal to` "cloud"
        actualLocalModel.current_temp `should equal` 123.56
    }

    @Test
    fun `get the current weather of the user from the local data store for a given place saved by the user`() {
        coEvery { dao.getBookmarkCurrentWeather("placeId") } returns expectedLocalModel
        val actualLocalModel = runBlocking { currentWeatherLocalDataStore.getCurrentData("placeId")!! }
        coVerify(exactly = 1) { dao.getBookmarkCurrentWeather("placeId") }

        actualLocalModel shouldBeInstanceOf CurrentWeatherLocalModel::class
        actualLocalModel.weather_type `should be equal to` "cloud"
        actualLocalModel.current_temp `should equal` 123.56
    }
}