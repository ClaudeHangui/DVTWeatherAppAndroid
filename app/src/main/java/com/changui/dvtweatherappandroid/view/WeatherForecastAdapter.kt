package com.changui.dvtweatherappandroid.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.changui.dvtweatherappandroid.databinding.WeatherForecastItemBinding
import com.changui.dvtweatherappandroid.domain.model.WeatherForecastUIModelListItem

class WeatherForecastAdapter(private val items: MutableList<WeatherForecastUIModelListItem>) :
    RecyclerView.Adapter<WeatherForecastViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherForecastViewHolder {
        return WeatherForecastViewHolder(
            WeatherForecastItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WeatherForecastViewHolder, position: Int) {
        val item = items[position]
        holder.itemBinding.dayLabel.text = item.day_of_week
        holder.itemBinding.currentTemp.text = "${item.current_temp}°"
        if (item.weather_type_separator != -1) {
            holder.itemBinding.weatherTypeSeparator.setImageResource(item.weather_type_separator)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setData(listItems: MutableList<WeatherForecastUIModelListItem>) {
        val diffUtils = WeatherForecastDiffUtils(items, listItems)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        items.clear()
        items.addAll(listItems)
        diffResult.dispatchUpdatesTo(this)
    }
}

class WeatherForecastViewHolder(val itemBinding: WeatherForecastItemBinding) : RecyclerView.ViewHolder(
    itemBinding.root
)