package com.server

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.ds.PGSimpleDataSource
import org.jetbrains.exposed.sql.*
import bemonke.models.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.logging.Logger

fun bananaPost(postTime: String) {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)
    transaction {
        val post = Posts
            .select { Posts.time eq postTime.replace("_", " ") }.singleOrNull()
        if (post != null) {
            val bananaValue = post[Posts.banana]
            Posts.update({ Posts.time eq postTime.replace("_", " ") }) {
                it[banana] = bananaValue + 1
            }
        }
    }
}

fun updateFriend(isAdd : Boolean, payload: UpdateFriendPayload) {
    val userId = payload.userId
    val friendId = payload.friendId
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)
    transaction {
        val user = Users
            .select { Users.userId eq userId }.singleOrNull()
        if (user != null) {
            val friends = Json.decodeFromString<MutableList<String>>(user[Users.friendList])
            if (!isAdd) {
                friends.remove(friendId)
            } else if (!friends.contains(friendId)) {
                friends.add(friendId)
            } else {
                return@transaction
            }
            Users.update({ Users.userId eq userId }) {
                it[friendList] = Json.encodeToString<MutableList<String>>(friends)
            }
        }
    }
}

fun addComment(payload: UpdateCommentPayload) {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)

    return transaction {
        Comments.insert {
            it[postUserId] = payload.postUserId
            it[commentUserId] = payload.commentUserId
            it[postTime] = payload.postTime
            it[commentTime] = payload.commentTime
            it[commentBody] = payload.commentBody
        }
    }
}

fun deleteComment(payload: UpdateCommentPayload) {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)

    return transaction {
        Comments.deleteWhere {
            (postUserId eq payload.postUserId) and
            (postTime eq payload.postTime) and
            (commentUserId eq payload.commentUserId) and
            (commentTime eq payload.commentTime)
        }
    }
}

fun editComment(payload: UpdateCommentPayload) {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)

    return transaction {
        Comments.update({(Comments.postUserId eq payload.postUserId) and
                (Comments.postTime eq payload.postTime) and
                (Comments.commentUserId eq payload.commentUserId) and
                (Comments.commentTime eq payload.commentTime)}) {
            it[commentBody] = payload.commentBody
        }
    }
}

fun updateLikePost(isLike : Boolean, payload: likePostPayload) {
    val logger = Logger.getLogger("LikePostLogger")
    logger.info("updateLikePost!!!!")
    val userId = payload.userId
    val post = payload.post
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)

    Database.connect(dataSource)
    return transaction {
        logger.info("postToLike!!!!")
        val postToLike = Posts.select { Posts.time eq post.time }.singleOrNull()
        logger.info("gotpostToLike!!!!")

        val likeList = postToLike?.get(Posts.userLikedList)?.let {
            Json.decodeFromString<MutableList<String>>(it)
        } ?: mutableListOf()

        if (!isLike) {
            logger.info("User $userId remove to like list")
            likeList.remove(userId)
        } else {
            logger.info("User $userId add to like list")
            likeList.add(userId)
        }

        Posts.update({ (Posts.time eq post.time) and (Posts.userId eq post.userId) }) {
            it[userLikedList] = Json.encodeToString(likeList)
            logger.info("like list updated:  $likeList")
        }

        likeList.size // Return the updated like count
    }
}
// pk for profile will be uid
// pk for images will be uid,timestamp
fun postPost(postToPost: DBPost) {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)
    println("HELLO1")
    transaction {
        Posts.insert {
            it[userId] = postToPost.userId
            it[username] = postToPost.username
            it[time] = postToPost.time
            it[caption] = postToPost.caption
            it[imageUrl] = postToPost.imageUrl
            it[userLikedList] = Json.encodeToString<MutableList<String>>(postToPost.userLikedList)
            it[banana] = postToPost.banana
            it[comments] = Json.encodeToString<MutableList<String>>(postToPost.comments)
        }
    }
    println("HELLO2")
}

fun deletePost(timeAndUserId: String) {
    val parts = timeAndUserId.split("@")

    // Access the parts as needed
    val postTime = parts[0]
    val userId = parts[1]

    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)

    transaction {
        Posts.deleteWhere { time eq postTime.replace("_", " ") and (Posts.userId eq userId) }
    }

//    println("Post deleted successfully")
}

fun editPostCaption(post: DBPost) {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)
    transaction {
        Posts.update({ (Posts.time eq post.time) and (Posts.userId eq post.userId) }) {
            it[caption] = post.caption
        }
    }
}

fun createUser(newUser: User) {
    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)
    transaction {
        Users.insert {
            it[userId] = newUser.userId
            it[username] = newUser.username
            it[profilePicUrl] = newUser.profilePicUrl
            it[friendList] = Json.encodeToString<MutableList<String>>(newUser.friendList)
        }
    }
}

fun updateProfilePic(params: String) {
    val parameters = params.replace("penguinz", "/")
    val parts = parameters.split("@")
    val userId = parts[0]
    val profilePicUrl = parts[1]

    val config = loadConfigFromResources("config.json")
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(config.url)
    dataSource.setUser(config.user)
    dataSource.setPassword(config.pass)
    Database.connect(dataSource)
    transaction {
        Users.update({ Users.userId eq userId }) {
            it[Users.profilePicUrl] = profilePicUrl
        }
    }

}