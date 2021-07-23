package com.changui.dvtweatherappandroid.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.changui.dvtweatherappandroid.domain.model.UserLocationModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlin.math.*

const val INTERVAL = 1000L
const val FASTEST_INTERVAL = 500L

class LocationLiveData(context: Context) : MutableLiveData<UserLocationModel>() {

    private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        context
    )
    private var previousLocation: Pair<Double, Double> = Pair(0.0, 0.0)
    private fun setLocationData(location: Location) {
        previousLocation = Pair(location.latitude, location.longitude)
        value = UserLocationModel(longitude = location.longitude, latitude = location.latitude)
    }

    // called when the lifecycle owner(LocationActivity) is either started or resumed
    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()

        startLocationUpdates()
    }

    override fun onInactive() {
        super.onInactive()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Initiate Location Updates using Fused Location Provider and
     * attaching callback to listen location updates
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    /**
     * Callback that triggers on location updates available
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            val result = locationResult.lastLocation

            val newLocation = Pair(result.latitude, result.longitude)
            if (distance(previousLocation.first, previousLocation.second, newLocation.first, newLocation.second) < 0.1) {
                // locations are equal
                Log.e(this@LocationLiveData::class.java.name, "previous and latest locations are equal")
            } else {
                setLocationData(result)
            }
        }
    }

    /**
     * Static object of location request
     */
    companion object {
        val locationRequest: LocationRequest = LocationRequest.create()
            .apply {
                interval = INTERVAL
                fastestInterval = FASTEST_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
    }

    private fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 3958.75 // in miles, change to 6371 for kilometer output

        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)

        val sindLat = sin(dLat / 2)
        val sindLng = sin(dLng / 2)

        val a = sindLat.pow(2.0) +
            (sindLng.pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)))

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c // output distance, in MILES
    }
}