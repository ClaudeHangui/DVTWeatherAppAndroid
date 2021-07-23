package com.changui.dvtweatherappandroid.domain.scope

import kotlinx.coroutines.CoroutineScope

interface CoroutineScopes {
    val main: CoroutineScope
    val io: CoroutineScope
    val default: CoroutineScope
}