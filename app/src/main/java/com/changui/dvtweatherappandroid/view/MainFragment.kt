package com.changui.dvtweatherappandroid.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.changui.dvtweatherappandroid.R
import com.changui.dvtweatherappandroid.databinding.MainFragmentBinding
import com.changui.dvtweatherappandroid.domain.error.Failure
import com.changui.dvtweatherappandroid.domain.model.CurrentWeatherUIModel
import com.changui.dvtweatherappandroid.domain.model.UserLocationModel
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem
import com.changui.dvtweatherappandroid.domain.result.ResultState
import com.changui.dvtweatherappandroid.presentation.MainViewModel
import com.changui.dvtweatherappandroid.presentation.showPermanentlyDeniedDialog
import com.changui.dvtweatherappandroid.presentation.showRationaleDialog
import com.changui.dvtweatherappandroid.presentation.showSnackBar
import com.fondesa.kpermissions.allDenied
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment), GoogleApiClient.ConnectionCallbacks {

    private lateinit var googleApiClient: GoogleApiClient.Builder
    private val viewModel: MainViewModel by viewModels()
    private lateinit var appContext: Context
    private var binding: MainFragmentBinding ? = null
    private lateinit var adapter: WeatherForecastAdapter
    private var locationModel: UserLocationModel ? = null
    private var itemTypeSeparator: Int = -1
    private var extraLatitude: Double = 0.0
    private var extraLongitude: Double = 0.0
    private var extraPlaceId: String = ""

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = WeatherForecastAdapter(mutableListOf())
        extraLatitude = activity?.intent?.getDoubleExtra("extra_latitude", 0.0) ?: 0.0
        extraLongitude = activity?.intent?.getDoubleExtra("extra_longitude", 0.0) ?: 0.0
        extraPlaceId = activity?.intent?.getStringExtra("extra_place_id") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)
        googleApiClient = GoogleApiClient.Builder(appContext)
        googleApiClient.addApi(LocationServices.API).addConnectionCallbacks(this).build()

        lifecycleScope.launchWhenResumed {
            val permissionRequest = permissionsBuilder(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).build()
            val result = permissionRequest.sendSuspend()
            when {
                result.allGranted() -> {
                    if (extraLatitude != 0.0 && extraLongitude != 0.0) renderLocation(
                        UserLocationModel(extraLongitude, extraLatitude, extraPlaceId)
                    ) else observeUserLocation()
                }
                result.allDenied() -> requireContext().showPermanentlyDeniedDialog(result)
                result.anyShouldShowRationale() -> requireContext().showRationaleDialog(
                    result,
                    permissionRequest
                )
            }
        }

        binding?.forecastWeatherRv?.setHasFixedSize(true)
        binding?.forecastWeatherRv?.adapter = adapter

        viewModel.getCurrentWeatherLiveData().observe(
            viewLifecycleOwner,
            { currentWeather: ResultState<CurrentWeatherUIModel> -> renderCurrentWeatherUI(currentWeather) }
        )

        viewModel.getForecastWeatherListLiveData().observe(
            viewLifecycleOwner,
            { items: ResultState<MutableList<WeatherForecastUIModelListItem>> -> renderForecastWeatherUI(items) }
        )

        /*
        viewModel.getForecastWeatherListFailureLiveData().observe(
            viewLifecycleOwner,
            { failure: ResultState<MutableList<WeatherForecastUIModelListItem>> ->
                renderForecastWeatherFailureState(
                    failure
                )
            }
        )
        */
        viewModel.getLoadingLiveData().observe(
            viewLifecycleOwner,
            { showLoading: Boolean -> setLoadingState(showLoading) }
        )
    }

    private fun setLoadingState(showLoading: Boolean) {
        binding?.progress?.visibility = if (showLoading && binding?.progress?.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    private fun renderForecastWeatherUI(resultState: ResultState<MutableList<WeatherForecastUIModelListItem>>) {
        when(resultState) {
            is ResultState.Success -> {
                setForecastWeatherData(resultState.data)
            }
            is ResultState.Error -> {
                val failureDescription = when (resultState.failure) {
                    is Failure.NetworkError -> getString(R.string.error_network_desc)
                    is Failure.ServerError -> getString(R.string.error_internal_server_desc)
                    is Failure.BadRequestError -> getString(R.string.error_forbidden_desc)
                    is Failure.GatewayError -> getString(R.string.error_gateway_desc)
                    else -> getString(R.string.error_unknown_desc)
                }

                if (resultState.data.isNullOrEmpty()) {
                    binding?.forecastWeatherErrorMessage?.text = failureDescription
                    binding?.forecastWeatherErrorMessageBtn?.setOnClickListener {
                        locationModel?.let { it1 ->
                            setLoadingState(true)
                            binding?.forecastWeatherErrorGroup?.visibility = View.GONE
                            binding?.currentWeatherErrorGroup?.visibility = View.GONE
                            viewModel.getForecastWeather(it1)
                        }
                    }
                    binding?.forecastWeatherErrorGroup?.visibility = View.VISIBLE
                } else {
                    setForecastWeatherData(resultState.data)
                    showSnackBar(failureDescription)
                }
            }
        }

    }

    private fun setForecastWeatherData(data: MutableList<WeatherForecastUIModelListItem>) {
        data.forEach { it.weather_type_separator = itemTypeSeparator }
        adapter.setData(data)
        binding?.forecastWeatherSuccessGroup?.visibility = View.VISIBLE
    }

    private fun renderCurrentWeatherUI(currentWeather: ResultState<CurrentWeatherUIModel>) {
        when(currentWeather) {
            is ResultState.Success -> {
                setCurrentWeatherData(currentWeather.data)
            }
            is ResultState.Error -> {
                val failureDescription = when (currentWeather.failure) {
                    is Failure.NetworkError -> getString(R.string.error_network_desc)
                    is Failure.ServerError -> getString(R.string.error_internal_server_desc)
                    is Failure.BadRequestError -> getString(R.string.error_forbidden_desc)
                    is Failure.GatewayError -> getString(R.string.error_gateway_desc)
                    else -> getString(R.string.error_unknown_desc)
                }

                if (currentWeather.data == CurrentWeatherUIModel.EMPTY) {
                    binding?.currentWeatherErrorMessage?.text = failureDescription
                    binding?.currentWeatherErrorMessageBtn?.setOnClickListener {
                        locationModel?.let { it1 ->
                            setLoadingState(true)
                            binding?.currentWeatherErrorGroup?.visibility = View.GONE
                            binding?.forecastWeatherErrorGroup?.visibility = View.GONE
                            viewModel.getCurrentWeather(it1)
                        }
                    }
                    binding?.currentWeatherErrorGroup?.visibility = View.VISIBLE
                } else {
                    currentWeather.data?.let { setCurrentWeatherData(it) }
                    showSnackBar(failureDescription)
                }
                locationModel?.let { viewModel.getForecastWeather(it) }
            }
        }
    }

    private fun setCurrentWeatherData(data: CurrentWeatherUIModel) {
        itemTypeSeparator = data.weather_type.toWeatherTypeSeparator().first
        locationModel?.let { viewModel.getForecastWeather(it) }
        binding?.weatherImageTop?.setImageResource(
            data.weather_type.toViewBackground().first
        )
        binding?.weatherImageBottom?.setBackgroundResource(
            data.weather_type.toViewBackground().second
        )
        binding?.currentWeatherTemp?.text = data.current_temp.toString().plus("°")
        binding?.currentWeatherLabel?.text = data.weather_type.toWeatherTypeSeparator().second.toUpperCase(
            Locale.getDefault()
        )
        binding?.minTempCurrentWeather?.text = data.min_temp.toTempLabel("min")
        binding?.currentTempCurrentWeather?.text = data.current_temp.toTempLabel(
            "current"
        )
        binding?.maxTempCurrentWeather?.text = data.max_temp.toTempLabel("max")

        binding?.currentWeatherSuccessGroup?.visibility = View.VISIBLE
        binding?.moreLabel?.setOnClickListener {
            startActivity(Intent(activity, BookmarksActivity::class.java))
        }
    }

    private fun observeUserLocation() {
        viewModel.getLocationData().observe(
            viewLifecycleOwner,
            { liveLocation: UserLocationModel -> renderLocation(liveLocation) }
        )
    }

    private fun renderLocation(liveLocation: UserLocationModel) {
        locationModel = liveLocation
        viewModel.getCurrentWeather(liveLocation)
    }

    override fun onConnected(p0: Bundle?) {
        Log.e(this::class.java.simpleName, "On google APi Client connected")
    }

    override fun onConnectionSuspended(p0: Int) {
        googleApiClient.build().connect()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}