package com.example.poopoo.api

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request

fun NewImgurApi() {
    val client = OkHttpClient()

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("image", "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7")
        .build()

    val request = Request.Builder()
        .url("https://api.imgur.com/3/image")
        .post(requestBody)
        .header("Authorization", "Client-ID 097e22d655f84f8")
        .build()
    GlobalScope.launch(Dispatchers.IO) {
        try {
            client.newCall(request).execute().use { response ->
                Log.d("test", "bruh")
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                else Log.d("test", "WORKED!")
                response.body!!.string()
            }
        } catch (e: Exception) {
            e.message?.let { Log.d("test", it) }
        }
    }
}