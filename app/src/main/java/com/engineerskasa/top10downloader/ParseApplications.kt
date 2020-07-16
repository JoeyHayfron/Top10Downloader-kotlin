package com.engineerskasa.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class ParseApplications {
    private val TAG = "ParseApplications"
    val application = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
        Log.d(TAG, "ParseData received: $xmlData")
        var status = true
        var inEntry = true
        var textValue = ""
        var gotImage = false

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName =
                    xpp.name?.toLowerCase()

                when (eventType) {

                    XmlPullParser.START_TAG -> {
                        Log.d(TAG, "parse: Starting tag for $tagName")
                        if (tagName == "entry") {
                            inEntry = true
                        }else if ((tagName == "image") && inEntry){
                            val imageResolution = xpp.getAttributeValue(null, "height")
                            gotImage = imageResolution == "53"
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xpp.text

                    XmlPullParser.END_TAG -> {
                        Log.d(TAG, "parse: End tag for $tagName")
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    application.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()
                                }
                                "name" -> currentRecord.name = textValue
                                "artist" -> currentRecord.artist = textValue
                                "releasedate" -> currentRecord.releaseDate = textValue
                                "summary" -> currentRecord.summary = textValue
                                "image" -> if(gotImage) currentRecord.imageURL = textValue
                            }
                        }
                    }
                }
                eventType = xpp.next()
            }
            for (app in application) {
                Log.d(TAG, "************************")
                Log.d(TAG, app.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }
        return status
    }
}