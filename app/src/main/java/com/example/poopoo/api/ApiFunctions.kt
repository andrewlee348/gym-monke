package com.example.poopoo.api

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import bemonke.models.*


fun ApiCreatePost(userId: String, username: String, time: String, image: Bitmap, caption: String) {
    uploadImageToImgur(image, onFinish = { imgurUrl ->
        run {
            try {
                Log.d("TAG", "imgurUrl: $imgurUrl")
                val post = DBPost(
                    userId = userId,
                    username = username,
                    time = time,
                    caption = caption,
                    imageUrl = imgurUrl,
                    userLikedList = mutableListOf(),
                    banana = 0,
                    comments = mutableListOf(),
                )
                GlobalScope.launch {
                    postPost(post)
                }
            } catch (e: Exception) {
                Log.d("test", "Failed to upload post: " + (e.message?: "."))
            }
        }
    })
}