package com.changui.dvtweatherappandroid.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.changui.dvtweatherappandroid.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            Log.e(this::class.java.name, "Activity launched")
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_container, MainFragment.newInstance())
                .commitNow()
        }
    }
}