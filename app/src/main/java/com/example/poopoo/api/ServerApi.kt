package com.example.poopoo.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import android.util.Log
import androidx.compose.runtime.*
import io.ktor.http.content.*
import kotlinx.serialization.encodeToString
import bemonke.models.*
import io.ktor.client.call.*

// SERVER CALLS

var SERVER_URL = ""
fun serverUrl() : String {
//    return "http://10.0.2.2:8080"
//    return "http://localhost:8080"
    return SERVER_URL
}

suspend fun bananaPost(post:Post) {
    val postTime = post.time.replace(" ", "_")
    val site = serverUrl() + "/banana_post/$postTime"
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(site)
    client.close()

    Log.d("ktor",response.toString())
}

suspend fun getUserFriends(userId: String): List<User> {
    val site = serverUrl() + "/get_user_friends/$userId"
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(site)
    client.close()

    val userFriends = Json.decodeFromString<List<User>>(response.bodyAsText())
    Log.d("ktor",userFriends.toString())
    return userFriends
}

// TODO: make a new route to get all users. Right now get_user_friends returns all users.
suspend fun getAllUsers(): List<User> {
    val site = serverUrl() + "/get_all_users"
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(site)
    client.close()
    val allUsers = Json.decodeFromString<List<User>>(response.bodyAsText())
    return allUsers
}

suspend fun createUser(user : User)  {
    try {
        val site = serverUrl() + "/create_user"
        val client = HttpClient(CIO)
        val jsonBody = Json.encodeToString(user)
        val response: HttpResponse = client.post(site) {
            contentType(ContentType.Application.Json)
            setBody(TextContent(jsonBody, ContentType.Application.Json)) // Set the JSON string as the request body
        }
        Log.d("ktor", response.toString())
        client.close()
    } catch (e: Exception) {
        Log.d("ktor", "Failed to create user: " + (e.message?: "."))
    }
}
suspend fun getUserData(userId:String): User {
    val site = serverUrl() + "/get_user_data/$userId"
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(site)
    client.close()

    val user = Json.decodeFromString<User>(response.bodyAsText())
    Log.d("ktor",response.toString())
    return user
}
suspend fun getUserPosts(userId:String): List<Post> {
    val site = serverUrl() + "/get_user_posts/$userId"
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(site)
    client.close()

    val userPosts = Json.decodeFromString<List<Post>>(response.bodyAsText())
    Log.d("ktor",response.toString())
    return userPosts
}

suspend fun getPosts(userId:String): List<Post> {
    val site = serverUrl() + "/get_posts/$userId"
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(site)
    client.close()

    val postList = Json.decodeFromString<List<Post>>(response.bodyAsText())
    Log.d("ktor",response.toString())
    return postList.reversed()
}

suspend fun postPost(postToPost: DBPost) {
    try {
        val site = serverUrl()+"/post_post"
        val client = HttpClient(CIO)
        val jsonBody = Json.encodeToString(postToPost)
        Log.d("testPost",jsonBody)
        val response: HttpResponse = client.post(site) {
            contentType(ContentType.Application.Json)
            setBody(TextContent(jsonBody, ContentType.Application.Json)) // Set the JSON string as the request body
        }
        Log.d("testPost2", response.toString())
        client.close()
    } catch (e: Exception) {
        Log.d("test", "Failed to upload post: " + (e.message?: "."))
    }
}

suspend fun deletePost(post:Post) {
    val postTime = post.time.replace(" ", "_")
    val site = serverUrl() + "/delete_post/$postTime@${post.userId}"
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(site)
    client.close()
    Log.d("ktor",response.toString())
}

suspend fun deleteComment(payload: UpdateCommentPayload) {
    val site = serverUrl() + "/delete_comment"
    val client = HttpClient(CIO)
    val jsonBody = Json.encodeToString<UpdateCommentPayload>(payload)
    client.delete(site) {
        contentType(ContentType.Application.Json)
        setBody(TextContent(jsonBody, ContentType.Application.Json))
    }
    client.close()
}

suspend fun updateProfilePic(userId:String, profilePicUrl:String) {
    var userIdAndProfilePicUrl = "${userId}@${profilePicUrl}"
    userIdAndProfilePicUrl=userIdAndProfilePicUrl.replace("/", "penguinz")
    Log.d("ktor",userIdAndProfilePicUrl
    )

    val site = serverUrl() + "/update_pfp/$userIdAndProfilePicUrl"
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(site)
    client.close()
    Log.d("ktor",response.toString())
}

suspend fun editPostCaption(postToPost: DBPost) {
    try {
        val site = serverUrl()+"/edit_post_caption"
        val client = HttpClient(CIO)
        val jsonBody = Json.encodeToString(postToPost)
        val response: HttpResponse = client.post(site) {
            contentType(ContentType.Application.Json)
            setBody(TextContent(jsonBody, ContentType.Application.Json)) // Set the JSON string as the request body
        }
        Log.d("test edit caption", response.toString())
        client.close()
    } catch (e: Exception) {
        Log.d("test", "Failed to edit post caption: " + (e.message?: "."))
    }
}

suspend fun likePost(payload: likePostPayload){
    val client = HttpClient(CIO)
    var site = serverUrl()+"/like_post"
    if (payload.userId in payload.post.userLikedList) {
        site = serverUrl() + "/unlike_post"
    }
    val jsonBody = Json.encodeToString<likePostPayload>(payload)
    client.put(site) {
        contentType(ContentType.Application.Json)
        setBody(TextContent(jsonBody, ContentType.Application.Json))
    }
    client.close()
}

suspend fun addFriend(payload: UpdateFriendPayload) {
    val site = serverUrl()+"/add_friend"
    val client = HttpClient(CIO)
    val jsonBody = Json.encodeToString<UpdateFriendPayload>(payload)
    client.put(site) {
        contentType(ContentType.Application.Json)
        setBody(TextContent(jsonBody, ContentType.Application.Json))
    }
    client.close()
}

suspend fun removeFriend(payload: UpdateFriendPayload) {
    val site = serverUrl()+"/remove_friend"
    val client = HttpClient(CIO)
    val jsonBody = Json.encodeToString<UpdateFriendPayload>(payload)
    client.put(site) {
        contentType(ContentType.Application.Json)
        setBody(TextContent(jsonBody, ContentType.Application.Json))
    }
    client.close()
}

suspend fun addComment(payload: UpdateCommentPayload) {
    val site = serverUrl()+"/add_comment"
    val client = HttpClient(CIO)
    val jsonBody = Json.encodeToString<UpdateCommentPayload>(payload)
    client.post(site) {
        contentType(ContentType.Application.Json)
        setBody(TextContent(jsonBody, ContentType.Application.Json))
    }
    client.close()
}

suspend fun getComments(payload: getCommentsPayload):ReturnCommentsPayload {
    val site = serverUrl()+"/get_comments"
    val client = HttpClient(CIO)
    val jsonBody = Json.encodeToString<getCommentsPayload>(payload)
    val response = client.get(site) {
        contentType(ContentType.Application.Json)
        setBody(TextContent(jsonBody, ContentType.Application.Json))
    }
    client.close()
    val comments = Json.decodeFromString<ReturnCommentsPayload>(response.bodyAsText())
    return comments
}