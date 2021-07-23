package com.changui.dvtweatherappandroid.domain.usecase

import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.domain.repository.WeatherLocationRepository
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.GetWeatherLocationBookmarksResult
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.GetWeatherLocationBookmarksUseCaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class GetWeatherLocationBookmarksUseCaseImplTest {
    private lateinit var repository: WeatherLocationRepository
    private lateinit var getWeatherLocationBookmarksUseCase: GetWeatherLocationBookmarksUseCaseImpl

    @BeforeEach
    fun setUp() {
        repository = mockk()
        getWeatherLocationBookmarksUseCase = GetWeatherLocationBookmarksUseCaseImpl(repository)
    }


    @Test
    fun `should return GetWeatherForecastBookmarksUsecaseSuccess when repository sends a list of bookmarks`() {
        val expectedSuccessResponse = mutableListOf(
            WeatherLocationBookmarkUIModel("placeID", "paris", "paris, dakar", 283.95, 50.1344),
            WeatherLocationBookmarkUIModel("placeID", "paris", "paris, dakar", 283.95, 50.1344),
            WeatherLocationBookmarkUIModel("placeID", "paris", "paris, dakar", 283.95, 50.1344)
        )

        coEvery { repository.getWeatherLocationBookmarks() } returns expectedSuccessResponse

        val actualWeatherForecastBookmarks = runBlocking {
            getWeatherLocationBookmarksUseCase.execute()
        }

        actualWeatherForecastBookmarks shouldBeInstanceOf  GetWeatherLocationBookmarksResult.GetWeatherLocationBookmarksSuccess::class
        actualWeatherForecastBookmarks `should equal` GetWeatherLocationBookmarksResult.GetWeatherLocationBookmarksSuccess(expectedSuccessResponse)
    }

}