package com.changui.dvtweatherappandroid

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DvtWeatherApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        Places.initialize(applicationContext, getString(R.string.dvt_google_place_api_key))
    }
}