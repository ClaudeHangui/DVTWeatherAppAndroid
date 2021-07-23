package com.changui.dvtweatherappandroid.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.changui.dvtweatherappandroid.databinding.BookmarkItemBinding
import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel

class BookmarksAdapter(
    private val items: MutableList<WeatherLocationBookmarkUIModel>,
    private val clickListener: BookmarkClickListener
) : RecyclerView.Adapter<BookmarksViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarksViewHolder {
        return BookmarksViewHolder(
            BookmarkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BookmarksViewHolder, position: Int) {
        val item = items[position]
        holder.itemBinding.name.text = item.location_address
        if (position == itemCount - 1) {
            holder.itemBinding.divider.visibility = View.GONE
        } else holder.itemBinding.divider.visibility = View.VISIBLE
        holder.itemBinding.root.setOnClickListener {
            clickListener.onItemClicked(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setData(dataSet: MutableList<WeatherLocationBookmarkUIModel>) {
        items.clear()
        items.addAll(dataSet)
        notifyDataSetChanged()
    }

    fun addToTop(item: WeatherLocationBookmarkUIModel) {
        items.add(0, item)
        notifyItemInserted(0)
    }
}

class BookmarksViewHolder(val itemBinding: BookmarkItemBinding) : RecyclerView.ViewHolder(
    itemBinding.root
)

interface BookmarkClickListener {
    fun onItemClicked(item: WeatherLocationBookmarkUIModel)
}