package com.server

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Posts: Table() {
    val userId: Column<String> = varchar("userid",100)
    val username: Column<String> = varchar("username",100)
    val time: Column<String> = varchar("time",50)
    val caption: Column<String> = varchar("caption", 1000)
    val imageUrl: Column<String> = varchar("imageurl", 100)
    val userLikedList: Column<String> = varchar("userLikedList", 10000)
    val banana: Column<Int> = integer("banana")
    val comments: Column<String> = varchar("comments", 10000)
    init {
        index(true, userId, time)
    }
}

object Comments: Table() {
    val postUserId: Column<String> = varchar("postUserId",100)
    val commentUserId: Column<String> = varchar("commentUserId", 100)
    val postTime: Column<String> = varchar("postTime",50)
    val commentTime: Column<String> = varchar("commentTime",50)
    val commentBody: Column<String> = varchar("commentBody",10000)
}

object Users: Table() {
    val userId: Column<String> = varchar("userid",100)
    val username: Column<String> = varchar("username", 100)
    val profilePicUrl: Column<String> = varchar("profilepicurl", 100)
    val friendList: Column<String> = varchar("friendlist", 10000)

    init {
        index(true, userId)
    }
}