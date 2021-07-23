package com.changui.dvtweatherappandroid.view

import android.text.SpannedString
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.changui.dvtweatherappandroid.R

fun String.toViewBackground(): Pair<Int, Int> {
    return when {
        contains("cloud", ignoreCase = true) -> Pair(R.drawable.forest_cloudy, R.color.cloudy)
        contains("rain", ignoreCase = true) -> Pair(R.drawable.forest_rainy, R.color.rainy)
        contains("sun", ignoreCase = true) -> Pair(R.drawable.forest_sunny, R.color.sunny)
        else -> Pair(R.drawable.forest_cloudy, R.color.cloudy)
    }
}

fun String.toWeatherTypeSeparator(): Pair<Int, String> {
    return when {
        contains("cloud", ignoreCase = true) -> Pair(R.drawable.clear, "cloudy")
        contains("rain", ignoreCase = true) -> Pair(R.drawable.rain, "rainy")
        contains("sun", ignoreCase = true) -> Pair(R.drawable.partly_sunny, "sunny")
        else -> Pair(R.drawable.clear, "cloudy")
    }
}

fun Double.toTempLabel(label: String): SpannedString {
    val temperatureValue = this
    return buildSpannedString {
        bold { appendLine("$temperatureValue°") }
        append(label)
    }
}