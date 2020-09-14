package com.altimetrik.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.altimetrik.database.AppDatabase
import com.altimetrik.model.Album
import com.altimetrik.model.ProcessingState
import com.altimetrik.model.ResponseData
import com.altimetrik.model.enum.Filter
import com.altimetrik.network.ApiClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Response
import java.util.*

class AlbumsViewModel(application: Application) : AndroidViewModel(application) {

    var selectedFilter: Filter = Filter.RELEASE_DATE
    private var responseLiveData: MutableLiveData<ProcessingState> = MutableLiveData()
    private var albumsOriginalList: MutableList<Album>? = mutableListOf()

    fun fetchInfoData(): MutableLiveData<ProcessingState> {
        fetchApiResponse().observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .doOnError { responseLiveData.postValue(ProcessingState.errorState) }
            .subscribe({processingState -> fetchUniqueAlbums(processingState)}, {t2: Throwable? -> responseLiveData.postValue(ProcessingState.errorState) })
        return responseLiveData
    }

    fun saveToDatabase(albums: MutableList<Album>): MutableLiveData<ProcessingState> {
        val liveData: MutableLiveData<ProcessingState> = MutableLiveData()
        saveAlbums(albums).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .doOnError { liveData.postValue(ProcessingState.errorState) }
            .subscribe()
        return liveData
    }

    private fun saveAlbums(albums: MutableList<Album>): Single<ProcessingState> {
        return Single.create<ProcessingState> {
            val db = Room.databaseBuilder(
                getApplication(),
                AppDatabase::class.java, "iTunes"
            ).build()
            db.albumDao().insertAll(albums)
        }
    }

    fun getSortedList(searchHint: String?) {
        responseLiveData.value = when (selectedFilter) {
            Filter.RELEASE_DATE -> getListInReleaseDateOrder(searchHint)
            Filter.COLLECTION_NAME -> getListInCollectionOrder(searchHint)
            Filter.TRACK_NAME -> getListInTrackOrder(searchHint)
            Filter.ARTIST_NAME -> getListInArtistOrder(searchHint)
            Filter.COLLECTION_PRICE -> getListInCollectionPrice(searchHint)
        }
    }

    private fun fetchUniqueAlbums(processingState: ProcessingState) {
        val responseData = processingState.extras as ResponseData
        val distinctList = removeDuplicates(responseData.albums)
        distinctList?.let { list ->
            for (item in list) {
                albumsOriginalList?.add(item)
            }
        }
        processingState.extras = albumsOriginalList
        responseLiveData.postValue(processingState)
    }

    fun getSpinnerItems(): List<String> {
        return arrayListOf(
            "Release Date",
            "Collection Name",
            "Track Name",
            "Artist Name",
            "Collection Price (Descending)"
        )
    }

    private fun fetchApiResponse(): Single<ProcessingState> {
        return Single.create<ProcessingState> {
            val db = Room.databaseBuilder(
                getApplication(),
                AppDatabase::class.java, "iTunes"
            ).build()
            val albums = db.albumDao().getAll()
            if (albums.isNullOrEmpty()) {
                val apiInterface = ApiClient.getClient()
                val call: Call<ResponseData> = apiInterface.fetchData()
                call.enqueue(object : retrofit2.Callback<ResponseData> {

                    override fun onResponse(
                        call: Call<ResponseData>,
                        response: Response<ResponseData>
                    ) {
                        val responseData = response.body()
                        ProcessingState.successState.extras = response.body()
                        it.onSuccess(ProcessingState.successState)
                    }

                    override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                        ProcessingState.errorState.error = t
                        it.onError(ProcessingState.errorState.error)
                    }
                })
            } else {
                val responseData = ResponseData(albums.size, albums)
                ProcessingState.dbSuccessState.extras = responseData
                it.onSuccess(ProcessingState.dbSuccessState)
            }
        }
    }

    private fun removeDuplicates(albums: List<Album>): MutableList<Album>? {
        albums.let {
            it.let { list ->
                return list.distinctBy { item -> item.trackName } as MutableList<Album>?
            }
        }
    }

    /**
     * Method to sort list in order of date
     */
    private fun getListInReleaseDateOrder(searchHint: String?): ProcessingState {
        val list: MutableList<Album>? = if (searchHint != null && searchHint.isNotEmpty())
            getSearchResult(searchHint)
        else albumsOriginalList
        return if (list.isNullOrEmpty()) {
            ProcessingState.noDataState
        } else {
            ProcessingState.dbSuccessState.extras =
                list.sortedBy { it.releaseDate } as MutableList<Album>?
            ProcessingState.dbSuccessState
        }
    }

    /**
     * Method to sort list in order of collection name
     */
    private fun getListInCollectionOrder(searchHint: String?): ProcessingState {
        val list: MutableList<Album>? = if (searchHint != null && searchHint.isNotEmpty())
            getSearchResult(searchHint)
        else albumsOriginalList
        return if (list.isNullOrEmpty()) {
            ProcessingState.noDataState
        } else {
            ProcessingState.dbSuccessState.extras =
                list.sortedBy { it.collectionName } as MutableList<Album>?
            ProcessingState.dbSuccessState
        }
    }

    /**
     * Method to sort list in order of track name
     */
    private fun getListInTrackOrder(searchHint: String?): ProcessingState {
        val list: MutableList<Album>? = if (searchHint != null && searchHint.isNotEmpty())
            getSearchResult(searchHint)
        else albumsOriginalList
        return if (list.isNullOrEmpty()) {
            ProcessingState.noDataState
        } else {
            ProcessingState.dbSuccessState.extras =
                list.sortedBy { it.trackName } as MutableList<Album>?
            ProcessingState.dbSuccessState
        }
    }

    /**
     * Method to sort list in order of artist name
     */
    private fun getListInArtistOrder(searchHint: String?): ProcessingState {
        val list: MutableList<Album>? = if (searchHint != null && searchHint.isNotEmpty())
            getSearchResult(searchHint)
        else albumsOriginalList
        return if (list.isNullOrEmpty()) {
            ProcessingState.noDataState
        } else {
            ProcessingState.dbSuccessState.extras =
                list.sortedBy { it.artistName } as MutableList<Album>?
            ProcessingState.dbSuccessState
        }
    }

    /**
     * Method to sort list in order of price
     */
    private fun getListInCollectionPrice(searchHint: String?): ProcessingState {
        val list: MutableList<Album>? = if (searchHint != null && searchHint.isNotEmpty())
            getSearchResult(searchHint)
        else albumsOriginalList
        return if (list.isNullOrEmpty()) {
            ProcessingState.noDataState
        } else {
            list.sortByDescending { it.collectionPrice ?: 0.0 }
            ProcessingState.dbSuccessState.extras = list
            ProcessingState.dbSuccessState
        }
    }

    /**
     * Method to search list
     */
    private fun getSearchResult(searchHint: String): MutableList<Album>? {
        return albumsOriginalList?.filter {
            it.trackName?.toLowerCase(Locale.getDefault())
                ?.contains(searchHint.toLowerCase(Locale.getDefault())) ?: false ||
                    it.artistName.toLowerCase(Locale.getDefault()).contains(
                        searchHint.toLowerCase(
                            Locale.getDefault()
                        )
                    ) ||
                    it.collectionName?.toLowerCase(Locale.getDefault())
                        ?.contains(searchHint.toLowerCase(Locale.getDefault())) ?: false
        } as MutableList<Album>?
    }
}