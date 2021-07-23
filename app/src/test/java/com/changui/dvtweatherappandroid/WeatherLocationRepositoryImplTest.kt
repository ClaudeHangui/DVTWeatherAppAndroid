package com.changui.dvtweatherappandroid

import com.changui.dvtweatherappandroid.data.WeatherLocationRepositoryImpl
import com.changui.dvtweatherappandroid.data.local.WeatherLocationBookmarkLocalModel
import com.changui.dvtweatherappandroid.data.local.WeatherLocationLocalDataStore
import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class WeatherLocationRepositoryImplTest {

    @MockK lateinit var weatherLocationLocalDataStore: WeatherLocationLocalDataStore
    private lateinit var weatherLocationRepositoryImpl: WeatherLocationRepositoryImpl
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        weatherLocationRepositoryImpl = WeatherLocationRepositoryImpl(weatherLocationLocalDataStore)
    }

    @Test
    fun `save weather location to bookmarks`() {
        val params = WeatherLocationBookmarkUIModel("placeID", "paris", "paris, dakar", 283.95, 50.1344)

        coEvery { weatherLocationLocalDataStore.saveToWeatherLocationBookmarks(params) } returns Unit
        runBlocking { weatherLocationRepositoryImpl.saveToBookmarksWeatherLocation(params) }
        coVerify { weatherLocationLocalDataStore.saveToWeatherLocationBookmarks(params) }
    }

    @Test
    fun `fetch weather location from bookmarks`() {
        val bookmarks = listOf(
            WeatherLocationBookmarkLocalModel("placeId", "Paris", "Paris, France", 123.45, 567.6))
        coEvery { weatherLocationLocalDataStore.getWeatherLocationBookmarks() } returns bookmarks
        val actualResponse = runBlocking { weatherLocationRepositoryImpl.getWeatherLocationBookmarks() }
        actualResponse.size `should be equal to` 1
        actualResponse[0].location_name shouldBeEqualTo "Paris"
    }
}