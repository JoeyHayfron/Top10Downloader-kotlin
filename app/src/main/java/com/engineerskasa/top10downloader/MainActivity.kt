package com.engineerskasa.top10downloader


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*


class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""
}

private const val TAG = "MainActivity"

private const val CURRENT_URL = "CURRENT_URL"
private const val CURRENT_LIMIT = "CURRENT_LIMIT"

class MainActivity : AppCompatActivity() {

    private var feedUrl: String =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10

    val viewModel: FeedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = FeedAdapter(this, R.layout.list_record, EMPTY_FEED_LIST)
        xmlListView.adapter = adapter
        viewModel.feedEntries.observe(this, Observer<List<FeedEntry>> { feedEntries ->  adapter.showList(feedEntries) })
        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(CURRENT_URL).toString()
            feedLimit = savedInstanceState.getInt(CURRENT_LIMIT)
        }

        viewModel.downloadUrl(feedUrl.format(feedLimit))
        Log.d(TAG, "onCreate: Finished")

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)

        if (feedLimit == 10) {
            menu?.findItem(R.id.mnu10)?.isChecked = true
        } else {
            menu?.findItem(R.id.mnu25)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuFree ->
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnuPaid ->
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnuSongs ->
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.mnu10, R.id.mnu25 ->
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(
                        TAG,
                        "onOptionsItemSelected: ${item.title} options selected with limit $feedLimit"
                    )
                } else {
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} options unchanged")
                }
            R.id.mnuRefresh ->
                viewModel.invalidate()

            else ->
                return super.onOptionsItemSelected(item)
        }
        viewModel.downloadUrl(feedUrl.format(feedLimit))
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_URL, feedUrl)
        outState.putInt(CURRENT_LIMIT, feedLimit)
    }

}