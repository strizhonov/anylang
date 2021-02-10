package com.strizhonovapps.anylangapp.service

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RequestSender {

    /**
     * Get JSON string from API by URL
     *
     * @param requestUrl url from where json to get
     * @return json string
     */
    fun sendGetRequestForJson(requestUrl: URL): String {
        val urlConnection = requestUrl.openConnection() as HttpURLConnection
        try {
            urlConnection.connect()
            val inputStream = urlConnection.inputStream
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val builder = StringBuilder()
                var line: String
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                    builder.append("\n")
                }
                return builder.toString()
            }
        } finally {
            urlConnection.disconnect()
        }
    }

}