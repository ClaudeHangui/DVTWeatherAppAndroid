package com.changui.dvtweatherappandroid.domain.result

import com.changui.dvtweatherappandroid.domain.error.Failure

interface ResultState {
    open class Error<T>(val error: Failure, val cacheData: T?) : ResultState
    open class Success<T>(val data: T) : ResultState
    interface Loading
}