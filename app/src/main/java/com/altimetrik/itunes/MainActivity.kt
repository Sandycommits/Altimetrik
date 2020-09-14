package com.altimetrik.itunes

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.altimetrik.adapter.AlbumsAdapter
import com.altimetrik.adapter.FilterAdapter
import com.altimetrik.model.Album
import com.altimetrik.model.ProcessingState
import com.altimetrik.model.State
import com.altimetrik.model.enum.Filter
import com.altimetrik.utils.OnCartListener
import com.altimetrik.utils.isConnected
import com.altimetrik.utils.snack
import com.altimetrik.viewmodel.AlbumsViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    AdapterView.OnItemSelectedListener, OnCartListener {

    private lateinit var albumsViewModel: AlbumsViewModel
    private var query = ""
    private lateinit var cartTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        albumsViewModel = AlbumsViewModel(application)
        spinner.onItemSelectedListener = this
        val adapter = FilterAdapter(
            this@MainActivity,
            R.layout.spinner_item,
            albumsViewModel.getSpinnerItems()
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
        albumsViewModel.fetchInfoData().observe(this, Observer { onFetchInformation(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        val searchView =
            menu.findItem(R.id.search).actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = getString(R.string.search_album)
        searchView.setOnQueryTextListener(this)

        val cartItemView = menu.findItem(R.id.action_cart).actionView as FrameLayout
        cartTextView = cartItemView.findViewById<TextView>(R.id.cart_badge)
        return true
    }

    private fun onFetchInformation(state: ProcessingState) {
        if (state.state == State.SUCCESS) {
            val responseData = state.extras as MutableList<Album>
            albumsViewModel.saveToDatabase(responseData)
            loadDate(responseData)
        } else if (state.state == State.DB_SUCCESS) {
            val responseData = state.extras as MutableList<Album>
            loadDate(responseData)
        } else if (state.state == State.NO_DATA) {
            rootLayout.snack(getString(R.string.no_matching_results))
        } else {
            rootLayout.snack(getString(R.string.no_network))
        }
    }

    private fun loadDate(albums: MutableList<Album>) {
        albumsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
            adapter = AlbumsAdapter(this@MainActivity, albums)
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        this.query = query!!
        albumsViewModel.getSortedList(query)
        return true
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        // Nothing to do onNothingSelected
    }

    override fun onItemSelected(
        adapterView: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        albumsViewModel.selectedFilter = when (position) {
            0 -> Filter.RELEASE_DATE
            1 -> Filter.COLLECTION_NAME
            2 -> Filter.TRACK_NAME
            3 -> Filter.ARTIST_NAME
            4 -> Filter.COLLECTION_PRICE
            else -> Filter.RELEASE_DATE
        }
        albumsViewModel.getSortedList(query)
    }

    override fun onCart(value: Int) {
        cartTextView.setText(value.toString())
    }
}