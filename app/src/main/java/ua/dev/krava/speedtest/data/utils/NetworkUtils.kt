package ua.dev.krava.speedtest.data.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Created by evheniikravchyna on 03.01.2018.
 */

fun OkHttpClient.makeCall(url: String): Response {
    val request = Request.Builder()
            .url(url)
            .build()
    return this.newCall(request).execute()
}