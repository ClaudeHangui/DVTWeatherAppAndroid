package com.changui.dvtweatherappandroid.domain.mapper

/**
 * A mapper that maps data from [Input] to [Output]
 */
interface Mapper<in Input, out Output> {
    fun map(input: Input): Output
}

interface MapperDualInput<in I1, in I2,  out Output> {
    fun map(input1: I1, inputI2: I2?): Output
}