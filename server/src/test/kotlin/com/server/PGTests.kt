package com.server

import kotlin.test.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.ds.PGSimpleDataSource
import org.jetbrains.exposed.sql.*
import bemonke.models.*
import io.ktor.server.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect

// Data to insert
val postsToInsert = listOf(
    DBPost("5721", "Irene", "2023-11-17 13:36:54", "dabbing on them haters", "https://i.imgur.com/J8XIcEl.jpg", mutableListOf(), 0, mutableListOf()),
    DBPost("5721", "Irene", "2023-11-17 13:48:35", "⛸️", "https://i.imgur.com/HqCzDN2.jpg", mutableListOf(), 0, mutableListOf()),
    DBPost("5721", "Irene", "2023-11-17 13:54:04", "pat pat", "https://i.imgur.com/jsVMLxv.jpg", mutableListOf(), 0, mutableListOf()),
    DBPost("9507", "Cathy", "2023-11-17 13:57:20", "rawr", "https://i.imgur.com/IGzRMEB.jpg", mutableListOf(), 0, mutableListOf()),
    DBPost("9507", "Cathy", "2023-11-17 13:58:06", "bruh", "https://i.imgur.com/JhKrxQy.jpg", mutableListOf(), 0, mutableListOf()),
    DBPost("9507", "Cathy", "2023-11-17 14:04:32", "huhh", "https://i.imgur.com/L5RYdo3.png", mutableListOf(), 0, mutableListOf())
)

val usersToInsert = listOf(
    User("9507","Cathy","https://i.imgur.com/tqJXoTX.jpg", mutableListOf("5721")),
    User("5721","Irene","https://i.imgur.com/jsqpRBX.jpg", mutableListOf("9507")),
    User("2012","Andrew","https://i.imgur.com/1Fw0PnL.jpg", mutableListOf("5721,9507"))
)

class PGTests {
    @Test
    fun test() {
        val config = loadConfigFromResources("config.json")
        val dataSource = PGSimpleDataSource()
        dataSource.setUrl(config.url)
        dataSource.setUser(config.user)
        dataSource.setPassword(config.pass)

        Database.connect(dataSource)
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users)
            SchemaUtils.createMissingTablesAndColumns(Posts)
        }
        transaction {
            // Use a loop or forEach to insert each post
            postsToInsert.forEach { post ->
                Posts.insert {
                    it[userId] = post.userId
                    it[username] = post.username
                    it[time] = post.time
                    it[caption] = post.caption
                    it[imageUrl] = post.imageUrl
                    it[userLikedList] = post.userLikedList?.let { Json.encodeToString(it) } ?: "[]"
                    it[banana] = post.banana
                    it[comments] = post.comments.let { Json.encodeToString(it) }
                }
            }
            usersToInsert.forEach { user ->
                Users.insert {
                    it[userId] = user.userId
                    it[username] = user.username
                    it[profilePicUrl] = user.profilePicUrl
                    it[friendList] = Json.encodeToString<MutableList<String>>(user.friendList)
                }
            }
        }
    }
}