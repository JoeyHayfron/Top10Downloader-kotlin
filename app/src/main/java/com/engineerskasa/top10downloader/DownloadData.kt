package com.engineerskasa.top10downloader

import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

private const val TAG = "DownloadData"

class DownloadData(private val callback: DownloadCallback) :
    AsyncTask<String, Void, String>() {

    interface DownloadCallback{
        fun OnDataAvailable(data: List<FeedEntry>)
    }
    override fun onPostExecute(result: String) {

        val parseApplications = ParseApplications()
        if(result != null){
            parseApplications.parse(result)

        }
        callback.OnDataAvailable(parseApplications.application)
    }

    override fun doInBackground(vararg params: String): String {
        Log.d(TAG, "doInBackground: passed parameter is ${params[0]}")
        val rssFeed = downloadXML(params[0])
        if (rssFeed.isEmpty()) {
            Log.d(TAG, "doInBackground: Error downloading")
        }
        return rssFeed
    }

    private fun downloadXML(urlPath: String): String {
        try{
            return URL(urlPath).readText()
        }catch (e: MalformedURLException){
            Log.d(TAG, "downloadData url error " + e.message)
        }catch (e: IOException){
            Log.d(TAG, "downloadData IO error " + e.message)
        }catch (e: SecurityException){
            Log.d(TAG, "downloadData: Security error " + e.message)
        }
        return ""
    }
}