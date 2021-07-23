package com.changui.dvtweatherappandroid.data.local

import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchers
import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchersImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class WeatherLocationLocalDataStoreImplTest {

    @MockK lateinit var dao: WeatherLocationDao
    @MockK lateinit var dispatchers: CoroutineDispatchers
    private lateinit var weatherLocationLocalDataStoreImpl: WeatherLocationLocalDataStoreImpl

    private val expectedList = listOf(
        WeatherLocationBookmarkLocalModel("placeId", "Paris", "Paris, France", 123.45, 567.6)
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery {
            dispatchers.io
        } returns CoroutineDispatchersImpl().io

        weatherLocationLocalDataStoreImpl = WeatherLocationLocalDataStoreImpl(dao)
    }

    @Test
    fun `calling getWeatherLocationBookmarks() returns weather locations that were saved` () {
        coEvery { dao.getAllWeatherLocationBookmarks() } returns expectedList

        val actualList = runBlocking { weatherLocationLocalDataStoreImpl.getWeatherLocationBookmarks() }
        coVerify { dao.getAllWeatherLocationBookmarks() }

        actualList.size `should be equal to` 1
    }

    @Test
    fun `calling saveToWeatherLocationBookmarks() saves weather location`() {
        val uiModelToSave = WeatherLocationBookmarkUIModel("placeId", "Paris", "Paris, France", 123.45, 567.6)
        coEvery { dao.saveToBookmarks(expectedList.first()) } returns Unit
        runBlocking { weatherLocationLocalDataStoreImpl.saveToWeatherLocationBookmarks(uiModelToSave) }
        coVerify { dao.saveToBookmarks(expectedList.first()) }
    }
}