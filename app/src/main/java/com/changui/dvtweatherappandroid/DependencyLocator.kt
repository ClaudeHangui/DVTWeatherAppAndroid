package com.changui.dvtweatherappandroid

import android.content.Context
import androidx.room.Room
import com.changui.dvtweatherappandroid.data.WeatherForecastRepositoryImpl
import com.changui.dvtweatherappandroid.data.WeatherLocationRepositoryImpl
import com.changui.dvtweatherappandroid.data.local.CurrentWeatherDao
import com.changui.dvtweatherappandroid.data.local.CurrentWeatherLocalDataStore
import com.changui.dvtweatherappandroid.data.local.CurrentWeatherLocalDataStoreImpl
import com.changui.dvtweatherappandroid.data.local.WeatherForecastDao
import com.changui.dvtweatherappandroid.data.local.WeatherForecastDb
import com.changui.dvtweatherappandroid.data.local.WeatherForecastLocalDataStore
import com.changui.dvtweatherappandroid.data.local.WeatherForecastLocalDataStoreImpl
import com.changui.dvtweatherappandroid.data.local.WeatherLocationDao
import com.changui.dvtweatherappandroid.data.local.WeatherLocationLocalDataStore
import com.changui.dvtweatherappandroid.data.local.WeatherLocationLocalDataStoreImpl
import com.changui.dvtweatherappandroid.data.mapper.CurrentWeatherMapper
import com.changui.dvtweatherappandroid.data.mapper.RemoteToLocalWeatherForecastMapper
import com.changui.dvtweatherappandroid.data.remote.CurrentWeatherFailureFactory
import com.changui.dvtweatherappandroid.data.remote.CurrentWeatherRemoteDataStore
import com.changui.dvtweatherappandroid.data.remote.CurrentWeatherRemoteDataStoreImpl
import com.changui.dvtweatherappandroid.data.remote.WeatherApiService
import com.changui.dvtweatherappandroid.data.remote.WeatherForecastFailureFactory
import com.changui.dvtweatherappandroid.data.remote.WeatherForecastRemoteDataStore
import com.changui.dvtweatherappandroid.data.remote.WeatherForecastRemoteDataStoreImpl
import com.changui.dvtweatherappandroid.domain.repository.WeatherForecastRepository
import com.changui.dvtweatherappandroid.domain.repository.WeatherLocationRepository
import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchers
import com.changui.dvtweatherappandroid.domain.scope.CoroutineDispatchersImpl
import com.changui.dvtweatherappandroid.domain.scope.CoroutineScopeImpl
import com.changui.dvtweatherappandroid.domain.scope.CoroutineScopes
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.BookmarkWeatherLocationUsecase
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.BookmarkWeatherLocationUsecaseImpl
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.GetWeatherLocationBookmarksUseCase
import com.changui.dvtweatherappandroid.domain.usecase.bookmarkweather.GetWeatherLocationBookmarksUseCaseImpl
import com.changui.dvtweatherappandroid.domain.usecase.currentweather.GetCurrentWeatherUseCase
import com.changui.dvtweatherappandroid.domain.usecase.currentweather.GetCurrentWeatherUseCaseImpl
import com.changui.dvtweatherappandroid.domain.usecase.weatherforecast.GetWeatherForecastListUsecase
import com.changui.dvtweatherappandroid.domain.usecase.weatherforecast.GetWeatherForecastListUsecaseImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val DB_NAME = "dvt_weather_forecast.db"

@InstallIn(SingletonComponent::class)
@Module(includes = [AbstractBindingProvision::class])
object NonStaticProvision {
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): WeatherForecastDb {
        return Room.databaseBuilder(
            appContext,
            WeatherForecastDb::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCurrentWeatherDao(database: WeatherForecastDb): CurrentWeatherDao {
        return database.currentWeatherDao()
    }

    @Provides
    fun provideWeatherForecastDao(database: WeatherForecastDb): WeatherForecastDao {
        return database.weatherForecastDao()
    }

    @Provides
    fun provideWeatherLocationDao(database: WeatherForecastDb): WeatherLocationDao {
        return database.weatherLocationDao()
    }

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun provideApiInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            var request = chain.request()
            val url = request.url.newBuilder().build()
            request = request.newBuilder().url(url).build()
            chain.proceed(request)
        }
    }

    @Provides
    fun provideOkHttpClient(
        apiInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .addInterceptor(apiInterceptor)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideCurrentWeatherErrorFactory(): CurrentWeatherFailureFactory {
        return CurrentWeatherFailureFactory()
    }

    @Provides
    fun provideWeatherForecastErrorFactory(): WeatherForecastFailureFactory {
        return WeatherForecastFailureFactory()
    }

    @Provides
    fun provideApi(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatchers {
        return CoroutineDispatchersImpl()
    }

    @Provides
    fun provideCoroutineScope(): CoroutineScopes {
        return CoroutineScopeImpl()
    }

    @Provides
    fun provideCurrentWeatherMapper(): CurrentWeatherMapper {
        return CurrentWeatherMapper()
    }

    @Provides
    fun provideForecastWeather(): RemoteToLocalWeatherForecastMapper {
        return RemoteToLocalWeatherForecastMapper()
    }
}

@InstallIn(SingletonComponent::class)
@Module
abstract class AbstractBindingProvision {
    @Binds
    abstract fun bindWeatherForecastRepository(
        weatherForecastRepository: WeatherForecastRepositoryImpl
    ): WeatherForecastRepository

    @Binds
    abstract fun bindWeatherLocationRepository(
        weatherLocationRepository: WeatherLocationRepositoryImpl
    ): WeatherLocationRepository

    @Binds
    abstract fun bindGetCurrentWeatherUseCase(
        getCurrentWeatherUseCase: GetCurrentWeatherUseCaseImpl
    ): GetCurrentWeatherUseCase

    @Binds
    abstract fun bindGetWeatherForecastListUseCase(
        getWeatherForecastListUsecase: GetWeatherForecastListUsecaseImpl
    ): GetWeatherForecastListUsecase

    @Binds
    abstract fun bindBookmarkWeatherLocationUseCase(
        bookmarkWeatherForecastUsecase: BookmarkWeatherLocationUsecaseImpl
    ): BookmarkWeatherLocationUsecase

    @Binds
    abstract fun bindGetBookmarkWeatherLocationUseCase(
        getWeatherLocationBookmarksUsecase: GetWeatherLocationBookmarksUseCaseImpl
    ): GetWeatherLocationBookmarksUseCase

    @Binds
    abstract fun bindWeatherForecastRemoteDataStore(
        weatherForecastRemoteDataStore: WeatherForecastRemoteDataStoreImpl
    ): WeatherForecastRemoteDataStore

    @Binds
    abstract fun bindCurrentWeatherRemoteDataStore(
        currentWeatherRemoteDataStore: CurrentWeatherRemoteDataStoreImpl
    ): CurrentWeatherRemoteDataStore

    @Binds
    abstract fun bindWeatherForecastLocalDataStore(
        weatherForecastLocalDataStore: WeatherForecastLocalDataStoreImpl
    ): WeatherForecastLocalDataStore

    @Binds
    abstract fun bindWeatherLocationLocalDataStore(
        weatherLocationLocalDataStore: WeatherLocationLocalDataStoreImpl
    ): WeatherLocationLocalDataStore

    @Binds
    abstract fun bindCurrentWeatherLocalDataStore(
        currentWeatherLocalDataStore: CurrentWeatherLocalDataStoreImpl
    ): CurrentWeatherLocalDataStore
}