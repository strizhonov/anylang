package com.strizhonovapps.anylangapp.service

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RequestSender {

    /**
     * Get JSON string from API by URL
     *
     * @param requestUrl url from where json to get
     * @return json string
     */
    fun sendGetRequestForJson(requestUrl: URL): String {
        return Executors.newSingleThreadExecutor().submit(Callable {
            val httpURLConnection = requestUrl.openConnection() as? HttpURLConnection
            httpURLConnection.run {
                try {
                    httpURLConnection?.connect()
                    val inputStream = httpURLConnection?.inputStream
                    return@run getJsonStringFromInputStream(inputStream)
                } finally {
                    httpURLConnection?.disconnect()
                }
            }
        })[5000, TimeUnit.MILLISECONDS]
    }

    private fun getJsonStringFromInputStream(inputStream: InputStream?): String {
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            val builder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
                builder.append("\n")
            }
            return builder.toString()
        }
    }

}