package com.changui.dvtweatherappandroid.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.changui.dvtweatherappandroid.R
import com.changui.dvtweatherappandroid.databinding.ActivityBookmarksBinding
import com.changui.dvtweatherappandroid.domain.model.WeatherLocationBookmarkUIModel
import com.changui.dvtweatherappandroid.presentation.BookmarksViewModel
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarksActivity : AppCompatActivity(), BookmarkClickListener {

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var binding: ActivityBookmarksBinding
    private val fields = listOf(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG
    )
    private val viewModel: BookmarksViewModel by viewModels()
    private lateinit var adapter: BookmarksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(findViewById(R.id.toolbar))
        this.title = "Bookmarks"
        adapter = BookmarksAdapter(mutableListOf(), this)
        binding.listContainer.userlist.setHasFixedSize(true)

        binding.listContainer.userlist.adapter = adapter

        viewModel.getWeatherLocationBookmarks()
        viewModel.getWeatherLocationBookmarkListLiveData().observe(
            this,
            { items: MutableList<WeatherLocationBookmarkUIModel> ->
                renderListState(
                    items
                )
            }
        )
        viewModel.getLoadingLiveData().observe(
            this,
            { showLoading: Boolean ->
                setLoadingState(
                    showLoading
                )
            }
        )

        binding.fab.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    private fun renderListState(items: MutableList<WeatherLocationBookmarkUIModel>) {
        if (items.size == 0) binding.listContainer.emptyBookmarkLabel.visibility = View.VISIBLE
        else {
            adapter.setData(items)
            binding.listContainer.userlist.visibility = View.VISIBLE
        }
    }

    private fun setLoadingState(showLoading: Boolean) {
        binding.listContainer.progress.visibility = if (showLoading && binding.listContainer.progress.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.e(this::class.java.name, "Place: ${place.id}, ${place.address}")
                        val newItem = WeatherLocationBookmarkUIModel(
                            place.id.orEmpty(),
                            place.name.orEmpty(),
                            place.address.orEmpty(),
                            place.latLng?.latitude ?: 0.0,
                            place.latLng?.longitude ?: 0.0
                        )

                        adapter.addToTop(newItem)

                        if (binding.listContainer.emptyBookmarkLabel.visibility == View.VISIBLE) {
                            binding.listContainer.emptyBookmarkLabel.visibility = View.GONE
                            binding.listContainer.userlist.visibility = View.VISIBLE
                        }
                        viewModel.saveWeatherLocation(
                            place.id.orEmpty(),
                            place.name.orEmpty(),
                            place.address.orEmpty(),
                            place.latLng?.latitude ?: 0.0,
                            place.latLng?.longitude ?: 0.0
                        )
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.e(this::class.java.name, status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(item: WeatherLocationBookmarkUIModel) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("extra_latitude", item.latitude)
            putExtra("extra_longitude", item.longitude)
            putExtra("extra_place_id", item.place_id)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.bookmark_map) {
            if (adapter.itemCount == 0) Toast.makeText(
                this,
                "You need to bookmark a position",
                Toast.LENGTH_SHORT
            ).show()
            else {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}