package com.changui.dvtweatherappandroid.data.remote.commonapimodel

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)