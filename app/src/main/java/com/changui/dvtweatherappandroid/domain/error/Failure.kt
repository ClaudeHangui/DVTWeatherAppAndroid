package com.changui.dvtweatherappandroid.domain.error

sealed class Failure {
    object NetworkError : Failure()
    object ServerError : Failure()
    object BadRequestError : Failure()
    object GatewayError : Failure()
    object UnknownError : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure(val message: String) : Failure()
}

data class FailureWithCache<T>(val failure: Failure, val data: T)