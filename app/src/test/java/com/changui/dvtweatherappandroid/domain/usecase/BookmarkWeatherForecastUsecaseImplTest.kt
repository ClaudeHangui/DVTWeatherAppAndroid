package com.changui.dvtweatherappandroid.domain.usecase

import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.domain.repository.WeatherLocationRepository
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.BookmarkWeatherLocationResult
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.BookmarkWeatherLocationUsecaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BookmarkWeatherForecastUsecaseImplTest {

    private lateinit var repository: WeatherLocationRepository
    private lateinit var bookmarkWeatherForecastUsecase: BookmarkWeatherLocationUsecaseImpl
    private val params = WeatherLocationBookmarkUIModel("placeId", "paris", "paris, France", 35.6783, -120.567)

    @BeforeEach
    fun setUp() {
        repository = mockk()
        bookmarkWeatherForecastUsecase = BookmarkWeatherLocationUsecaseImpl(repository)
    }

    @Test
    fun `should call the repository to bookmark a forecast weather item`(){
        coEvery { repository.saveToBookmarksWeatherLocation(params) } returns Unit
        val actualResponse = runBlocking { bookmarkWeatherForecastUsecase.execute(params) }
        coVerify(exactly = 1) { repository.saveToBookmarksWeatherLocation(params) }
        actualResponse shouldBeInstanceOf  BookmarkWeatherLocationResult::class
    }

}