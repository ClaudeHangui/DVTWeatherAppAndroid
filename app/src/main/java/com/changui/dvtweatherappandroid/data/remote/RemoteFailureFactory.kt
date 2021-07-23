package com.changui.dvtweatherappandroid.data.remote

import com.changui.dvtweatherappandroid.domain.error.Failure
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection

interface RemoteBaseFailureFactory {
    fun produce(exception: Exception): Failure
}

abstract class RemoteBaseFailureFactoryImpl : RemoteBaseFailureFactory {
    override fun produce(exception: Exception): Failure {
        return when (exception) {
            is IOException -> Failure.NetworkError
            is HttpException -> {
                val response = exception.response()
                return handleHttpErrorCode(response)
            }
            else -> Failure.UnknownError
        }
    }

    private fun <T> handleHttpErrorCode(response: Response<T>?): Failure {
        return when (response?.code()) {
            HttpURLConnection.HTTP_INTERNAL_ERROR -> Failure.ServerError
            HttpURLConnection.HTTP_BAD_GATEWAY -> Failure.GatewayError
            HttpURLConnection.HTTP_BAD_REQUEST -> Failure.BadRequestError
            else -> Failure.UnknownError
        }
    }
}