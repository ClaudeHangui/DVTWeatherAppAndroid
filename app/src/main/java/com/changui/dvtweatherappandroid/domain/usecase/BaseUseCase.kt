package com.changui.dvtweatherappandroid.domain.usecase

import com.changui.dvtweatherappandroid.domain.result.ResultState

interface UseCaseWithoutParam<out T : ResultState> {
    suspend fun execute(): T
}

interface UseCaseWithParams<out T : ResultState, in Params> {
    suspend fun execute(params: Params): T
}