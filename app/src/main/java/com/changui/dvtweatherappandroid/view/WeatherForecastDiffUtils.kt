package com.changui.dvtweatherappandroid.view

import androidx.recyclerview.widget.DiffUtil
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem

class WeatherForecastDiffUtils(
    private val oldList: List<WeatherForecastUIModelListItem>,
    private val newList: List<WeatherForecastUIModelListItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }
}