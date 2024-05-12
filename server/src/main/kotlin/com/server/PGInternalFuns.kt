package com.server

import bemonke.models.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.ds.PGSimpleDataSource

fun turnDBPostIntoPost(postList: List<DBPost>): List<Post> {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)

    val returnList = mutableListOf<Post>()

    transaction {
        for (post in postList) {
            val result = Users.select{Users.userId eq post.userId}.single()

            val userId = post.userId
            val username = post.username
            val profilePicUrl = result[Users.profilePicUrl]
            val time = post.time
            val caption = post.caption
            val imageUrl = post.imageUrl
            val banana = post.banana
            val post = Post(userId, username, profilePicUrl, time, caption, imageUrl, mutableListOf(), banana, mutableListOf())
            returnList.add(post)
        }
    }

    return returnList
}

