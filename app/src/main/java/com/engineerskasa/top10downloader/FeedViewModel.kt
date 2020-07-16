package com.engineerskasa.top10downloader

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

private const val TAG = "FeedViewModel"
val EMPTY_FEED_LIST: List<FeedEntry> = Collections.emptyList()

class FeedViewModel : ViewModel(), DownloadData.DownloadCallback {
    private var downloadData: DownloadData? = null
    private var fetchedUrlCached = "INVALIDATED"

    private val feed = MutableLiveData<List<FeedEntry>>()
    val feedEntries : LiveData<List<FeedEntry>>
        get() = feed

    init {
        feed.postValue(EMPTY_FEED_LIST)
    }

    fun downloadUrl(feedUrl: String) {
        if (feedUrl != fetchedUrlCached) {
            Log.d(TAG, "ondoInBackGround: Started")
            downloadData = DownloadData(this)
            downloadData?.execute(feedUrl)
            Log.d(TAG, "onCreate: Finished")
            fetchedUrlCached = feedUrl
        } else
            Log.d(TAG, "downloadUrl: The url hasn't changed")
    }

    fun invalidate() {
        fetchedUrlCached = "INVALIDATED"
    }

    override fun OnDataAvailable(data: List<FeedEntry>) {
        Log.d(TAG, "onDataAvailable: Data is $data")
        feed.value = data
    }

    override fun onCleared() {
        downloadData?.cancel(true)
    }
}