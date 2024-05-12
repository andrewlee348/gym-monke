package com.server

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.ds.PGSimpleDataSource
import org.jetbrains.exposed.sql.*
import bemonke.models.*
import kotlinx.serialization.json.Json
import java.util.logging.Logger

suspend fun getUserData(userId : String): User {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)
    var userToReturn = User("6969", "Error: No User Signed in", "no profile pic", mutableListOf())

    transaction {
        val user = Users.select{Users.userId eq userId}.singleOrNull()
        if (user != null){
            // Iterate through the results and create Post objects
            val userId1 = user[Users.userId]
            val username = user[Users.username]
            val profilePicUrl = user[Users.profilePicUrl]
            val friendList = Json.decodeFromString<MutableList<String>>(user[Users.friendList])
            userToReturn = User(userId1, username, profilePicUrl, friendList)
        }
    }
    return userToReturn
}

suspend fun getAllUsers() : List<User> {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)
    val returnUsers = mutableListOf<User>()

    transaction {
        // Select all rows from the "posts" table
        val result = Users.selectAll()

        // Iterate through the results and create Post objects
        for (row in result) {
            val userId = row[Users.userId]
            val username = row[Users.username]
            val profilePicUrl = row[Users.profilePicUrl]
            val friendList = Json.decodeFromString<MutableList<String>>(row[Users.friendList])

            val user = User(userId, username, profilePicUrl, friendList)
            returnUsers.add(user)
        }
    }
    return returnUsers
}

suspend fun getFriendListUserData(userId : String): List<User> {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)
    val returnUsers = mutableListOf<User>()

    transaction {
        // Select all rows from the "posts" table
        val result = Users.selectAll()
        var listToSearch = emptyList<String>()

        val userToSearch = Users.select { Users.userId eq userId }.singleOrNull()
        listToSearch = emptyList<String>()

        if (userToSearch != null) {
            listToSearch = Json.decodeFromString<MutableList<String>>(userToSearch[Users.friendList])
        }

        // Iterate through the results and create Post objects
        for (row in result) {
            var friendFound = false
            for (idx in 0..listToSearch.size-1) {
                if (listToSearch[idx] == row[Users.userId]) {
                    friendFound = true
                    break
                }
            }
            if (!friendFound) {
                continue
            }

            val userId = row[Users.userId]
            val username = row[Users.username]
            val profilePicUrl = row[Users.profilePicUrl]
            val friendList = Json.decodeFromString<MutableList<String>>(row[Users.friendList])

            val user = User(userId, username, profilePicUrl, friendList)
            returnUsers.add(user)
        }
    }
    return returnUsers
}

// gets all posts if no userId is passed in
suspend fun getUserPosts(userId: String? = null):List<Post> {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)
    val DBPosts = mutableListOf<DBPost>()
    val returnList = mutableListOf<Post>()

    transaction {
        // Select all rows from the "posts" table
        val result = if (userId != null) {
            Posts.select { Posts.userId eq userId }
        } else {
            Posts.selectAll()
        }

        // Iterate through the results and create Post objects
        for (row in result) {
            val userId = row[Posts.userId]
            val username = row[Posts.username]
            val time = row[Posts.time]
            val caption = row[Posts.caption]
            val imageUrl = row[Posts.imageUrl]
            val userLikedList = Json.decodeFromString<MutableList<String>>(row[Posts.userLikedList])
            val banana = row[Posts.banana]
            val comments = Json.decodeFromString<MutableList<String>>(row[Posts.comments])
            val post = DBPost(userId, username, time, caption, imageUrl, userLikedList, banana, comments)
            DBPosts.add(post)
        }

        for (post in DBPosts) {
            val result = Users.select{Users.userId eq post.userId}.singleOrNull()
            if (result == null) {
                continue
            }

            val userId = post.userId
            val username = post.username
            val profilePicUrl = result[Users.profilePicUrl]
            val time = post.time
            val caption = post.caption
            val imageUrl = post.imageUrl
            val userLikedList = post.userLikedList
            val banana = post.banana
            val comments = post.comments
            val post = Post(userId, username, profilePicUrl, time, caption, imageUrl, userLikedList, banana, comments)
            returnList.add(post)
        }
    }
    return returnList
}

fun getComments(payload: getCommentsPayload):ReturnCommentsPayload {
    val commentsToReturn = ReturnCommentsPayload(payload.postUserId, payload.postTime, mutableListOf<CommentData>())

    transaction {
        val result = Comments.select {
            (Comments.postUserId eq payload.postUserId) and (Comments.postTime eq payload.postTime)
        }
        for (comment in result) {
            val commenter = Users.select { Users.userId eq comment[Comments.commentUserId] }.singleOrNull()
            if (commenter != null) {
                commentsToReturn.comments.add(
                    CommentData(
                        comment[Comments.commentUserId],
                        comment[Comments.commentTime],
                        comment[Comments.commentBody],
                        commenter[Users.profilePicUrl],
                        commenter[Users.username],
                    )
                )
            }
        }
    }
    return commentsToReturn
}

// gets all posts if no userId is passed in
suspend fun getFriendPosts(userId: String):List<Post> {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)

    var DBPosts = mutableListOf<DBPost>()
    val returnList = mutableListOf<Post>()

    var friendList = mutableListOf<String>()

    transaction {
        val user = Users.select{Users.userId eq userId}.singleOrNull()
        if (user != null){
            friendList = Json.decodeFromString<MutableList<String>>(user[Users.friendList])
        }
    }
    val logger = Logger.getLogger("chocolate")
    logger.info("friendList:")
    for (friend in friendList) {
        logger.info(friend)
    }

    transaction {
        val result = mutableListOf<ResultRow>() // Assuming Row is the type returned by Posts.select

        for (friendId in friendList) {
            result.addAll(Posts.select { Posts.userId eq friendId })
        }
        // add my own
        result.addAll(Posts.select { Posts.userId eq userId })

        // Iterate through the results and create Post objects
        for (row in result) {
            val userId = row[Posts.userId]
            val username = row[Posts.username]
            val time = row[Posts.time]
            val caption = row[Posts.caption]
            val imageUrl = row[Posts.imageUrl]
            val userLikedList = Json.decodeFromString<MutableList<String>>(row[Posts.userLikedList])
            val banana = row[Posts.banana]
            val comments = Json.decodeFromString<MutableList<String>>(row[Posts.comments])
            val post = DBPost(userId, username, time, caption, imageUrl, userLikedList, banana, comments)
            DBPosts.add(post)
        }

        // Sort posts by time
        DBPosts = DBPosts.sortedBy { it.time }.toMutableList()


        for (post in DBPosts) {
            val result = Users.select{Users.userId eq post.userId}.singleOrNull()
            if (result == null) {
                continue
            }

            val userId = post.userId
            val username = post.username
            val profilePicUrl = result[Users.profilePicUrl]
            val time = post.time
            val caption = post.caption
            val imageUrl = post.imageUrl
            val userLikedList = post.userLikedList
            val banana = post.banana
            val comments = post.comments
            val post = Post(userId, username, profilePicUrl, time, caption, imageUrl, userLikedList, banana, comments)
            returnList.add(post)
        }
    }
    return returnList
}