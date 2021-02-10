package com.strizhonovapps.anylangapp.service

import android.util.Log
import java.net.InetAddress
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val ADDRESS = "google.com"

class InternetChecker {

    fun isInternetAvailable() = try {
        Executors.newSingleThreadExecutor().submit(Callable {
            "" != InetAddress.getByName(ADDRESS).toString()
        })[5000, TimeUnit.MILLISECONDS]!!
    } catch (e: Exception) {
        Log.e(this.javaClass.simpleName, "Unable to check the internet status.")
        false
    }

}