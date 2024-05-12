package com.example.poopoo.api

import android.graphics.Bitmap
import android.util.Log
import bemonke.models.Post
import bemonke.models.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun filterList(inputArray: List<User>, prefix: String): List<User> {
    return inputArray.filter { it.username.startsWith(prefix) }.toList()
}