package com.server.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.server.*
import io.ktor.http.*
import io.ktor.server.request.*
import kotlinx.serialization.encodeToString

import kotlinx.serialization.json.Json
import bemonke.models.*
import kotlinx.serialization.decodeFromString

fun getProfile():Int {
    return 2
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/get_all_users") {
            val usersToSend = getAllUsers()
            val response = Json.encodeToString(usersToSend)
            call.respond(response)
        }
        get("/get_user_friends/{userId}") {
            val tempUserId = call.parameters["userId"]
            if (tempUserId != null) {
                val userFriendsData = getFriendListUserData(tempUserId)
                val response = Json.encodeToString(userFriendsData)
                call.respond(response)
            }
        }
        get("/get_user_posts/{userId}") {
            val userPosts = getUserPosts(call.parameters["userId"])
            val response = Json.encodeToString(userPosts)
            call.respond(response)
        }
        get("/get_user_data/{userId}") {
            val userId= call.parameters["userId"]?:"1"
            val userData = getUserData(userId)
            val response = Json.encodeToString(userData)
            call.respond(response)
        }
        get("/get_profile") {
            val profileData = getProfile()
            call.respond(profileData)
        }
        get("/get_posts/{userId}") {
            val allPosts = getFriendPosts(call.parameters["userId"]?:"bruh")
            val response = Json.encodeToString(allPosts)
            call.respond(response)
        }
        get("/banana_post/{time}") {
            bananaPost(call.parameters["time"]?:"bruh")
        }
        get("/delete_post/{timeAndUserId}") {
            deletePost(call.parameters["timeAndUserId"]?:"bruh")
        }
        get("/update_pfp/{x}") {
            updateProfilePic(call.parameters["x"]?:"bruh")
        }
        put("/add_friend") {
            val payload = Json.decodeFromString<UpdateFriendPayload>(call.receiveText())
            updateFriend(true,payload)
        }
        put("/remove_friend") {
            val payload = Json.decodeFromString<UpdateFriendPayload>(call.receiveText())
            updateFriend(false,payload)
        }
        post("/add_comment") {
            val payload = Json.decodeFromString<UpdateCommentPayload>(call.receiveText())
            addComment(payload)
        }
        delete("/delete_comment") {
            val payload = Json.decodeFromString<UpdateCommentPayload>(call.receiveText())
            deleteComment(payload)
        }
        put("edit_comment") {
            val payload = Json.decodeFromString<UpdateCommentPayload>(call.receiveText())
            editComment(payload)
        }
        get("get_comments") {
            val payload = Json.decodeFromString<getCommentsPayload>(call.receiveText())
            val commentsToReturn = getComments(payload)
            call.respond(Json.encodeToString(commentsToReturn))
        }
        put("/like_post") {
            val payload = Json.decodeFromString<likePostPayload>(call.receiveText())
            val response = updateLikePost(true,payload)
            call.respond(response)
        }
        put("/unlike_post") {
            val payload = Json.decodeFromString<likePostPayload>(call.receiveText())
            val response = updateLikePost(false,payload)
            call.respond(response)
        }
        post("/edit_post_caption") {
            try {
                val post = Json.decodeFromString<DBPost>(call.receiveText())
                editPostCaption(post)
            } catch (e: Exception) {
                call.respondText("Failed to edit the post", status = HttpStatusCode.BadRequest)
            }
        }
        post("/post_post") {
            println("HELLO3")
            try {
                println("HELLO4")
                val post = Json.decodeFromString<DBPost>(call.receiveText())
                postPost(post)
                call.respondText("Post stored correctly", status = HttpStatusCode.Created)
            } catch (e: Exception) {
                // Handle any exceptions, e.g., validation errors or database errors
                call.respondText("Failed to store the post", status = HttpStatusCode.BadRequest)
            }
//            val post = call.receive<HttpResponse>()
//            val post2 = Json.decodeFromString<Post>(post.bodyAsText())
//            postPost(post2)
//            call.respondText("Post stored correctly", status = HttpStatusCode.Created)
        }
        post("/create_user") {
            try {
                val newUser = Json.decodeFromString<User>(call.receiveText())
                createUser(newUser)
                call.respondText("User created correctly", status = HttpStatusCode.Created)
            } catch (e: Exception) { call.respondText("Failed create user", status = HttpStatusCode.BadRequest) }
        }
    }
}
