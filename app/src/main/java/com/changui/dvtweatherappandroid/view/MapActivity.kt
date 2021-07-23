package com.changui.dvtweatherappandroid.view

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.changui.dvtweatherappandroid.R
import com.changui.dvtweatherappandroid.databinding.ActivityMapBinding
import com.changui.dvtweatherappandroid.domain.model.UserLocationModel
import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.presentation.BookmarksViewModel
import com.changui.dvtweatherappandroid.presentation.showPermanentlyDeniedDialog
import com.changui.dvtweatherappandroid.presentation.showRationaleDialog
import com.fondesa.kpermissions.allDenied
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private lateinit var binding: ActivityMapBinding
    private val viewModel: BookmarksViewModel by viewModels()
    private lateinit var bookMarkGoogleMap: GoogleMap
    private lateinit var googleApiClient: GoogleApiClient.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        googleApiClient = GoogleApiClient.Builder(this)
        googleApiClient.addApi(LocationServices.API).addConnectionCallbacks(this).build()

        lifecycleScope.launchWhenResumed {
            val permissionRequest = permissionsBuilder(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).build()
            val result = permissionRequest.sendSuspend()
            when {
                result.allGranted() -> { observeUserLocation() }
                result.allDenied() -> this@MapActivity.showPermanentlyDeniedDialog(result)
                result.anyShouldShowRationale() -> this@MapActivity.showRationaleDialog(
                    result,
                    permissionRequest
                )
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        viewModel.getWeatherLocationBookmarks()

        viewModel.getLoadingLiveData().observe(
            this,
            { showLoading: Boolean -> setLoadingState(showLoading) }
        )

        viewModel.getWeatherLocationBookmarkListLiveData().observe(
            this,
            { items: MutableList<WeatherLocationBookmarkUIModel> -> renderMapState(items) }
        )
    }

    private fun renderMapState(items: MutableList<WeatherLocationBookmarkUIModel>) {
        items.forEach {
            bookMarkGoogleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude)).anchor(0.5f, 0.5f).title(
                        it.location_name
                    )
            )
        }
    }

    private fun observeUserLocation() {
        viewModel.getLocationData().observe(
            this,
            { liveLocation: UserLocationModel -> addMarkerToMap(liveLocation) }
        )
    }

    private fun addMarkerToMap(liveLocation: UserLocationModel) {
        bookMarkGoogleMap.apply {
            val myPosition = LatLng(liveLocation.latitude, liveLocation.longitude)
            addMarker(MarkerOptions().position(myPosition).anchor(0.5f, 0.5f).title("My position"))
        }
    }

    private fun setLoadingState(showLoading: Boolean) {
        binding.progress.visibility = if (showLoading && binding.progress.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        bookMarkGoogleMap = googleMap
        bookMarkGoogleMap.uiSettings.isZoomControlsEnabled = true
    }

    override fun onConnected(p0: Bundle?) {
        Log.e(this::class.java.simpleName, "On google APi Client connected")
    }

    override fun onConnectionSuspended(p0: Int) {
        googleApiClient.build().connect()
    }
}